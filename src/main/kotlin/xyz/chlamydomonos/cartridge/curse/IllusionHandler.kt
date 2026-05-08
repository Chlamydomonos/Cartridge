package xyz.chlamydomonos.cartridge.curse

import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.player.PlayerModel
import net.minecraft.client.renderer.entity.state.AvatarRenderState
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.PlayerModelType
import net.minecraft.world.entity.player.PlayerSkin
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import xyz.chlamydomonos.cartridge.loaders.EffectLoader
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@EventBusSubscriber(value = [Dist.CLIENT])
object IllusionHandler {
    data class GhostModel(
        var pos: Vec3,
        var rot: Float,
        var dieTick: Int,
        val skin: PlayerSkin
    )

    private val wideModel by lazy {
        PlayerModel(Minecraft.getInstance().entityModels.bakeLayer(ModelLayers.PLAYER), false)
    }
    private val slimModel by lazy {
        PlayerModel(Minecraft.getInstance().entityModels.bakeLayer(ModelLayers.PLAYER_SLIM), true)
    }

    private val ghosts = mutableSetOf<GhostModel>()

    fun addGhosts() {
        val skins = mutableListOf<PlayerSkin>()
        val mc = Minecraft.getInstance()
        val level = mc.level ?: return
        val player = mc.player ?: return
        val connection = mc.connection
        if (connection != null) {
            skins.addAll(connection.onlinePlayers.map { it.skin })
        }
        skins.addAll(DefaultPlayerSkin.DEFAULT_SKINS)

        val random = level.random
        val ghostCount = random.nextInt(1, 8)
        repeat(ghostCount) {
            val angle = random.nextDouble() * 2 * PI
            val distance = random.nextDouble() * 8
            val targetX = player.x + cos(angle) * distance
            val targetZ = player.z + sin(angle) * distance
            var targetY = player.y

            val mutablePos = BlockPos.MutableBlockPos(targetX, targetY + 5.0, targetZ)
            var foundGround = false
            repeat(13) {
                val block = level.getBlockState(mutablePos)
                if (!block.getCollisionShape(level, mutablePos).isEmpty) {
                    targetY = mutablePos.y + 1.0
                    foundGround = true
                    return@repeat
                }
                mutablePos.move(Direction.DOWN)
            }

            if (!foundGround) {
                targetY = player.y
            }

            val rot = random.nextFloat() * 360f
            val pos = Vec3(targetX, targetY, targetZ)
            val skin = skins.random()
            ghosts.add(GhostModel(pos, rot, player.tickCount + 200, skin))
        }
    }

    @SubscribeEvent
    fun onRenderLevel(event: RenderLevelStageEvent.AfterOpaqueBlocks) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        if (!player.hasEffect(EffectLoader.CURSE)) return

        val cameraPos = event.levelRenderState.cameraRenderState.pos
        val poseStack = event.poseStack
        val bufferSource = mc.renderBuffers().bufferSource()
        val iterator = ghosts.iterator()
        while (iterator.hasNext()) {
            val ghost = iterator.next()
            if (player.tickCount > ghost.dieTick) {
                iterator.remove()
                continue
            }

            poseStack.pushPose()
            val x = ghost.pos.x - cameraPos.x
            val y = ghost.pos.y - cameraPos.y
            val z = ghost.pos.z - cameraPos.z
            poseStack.translate(x, y, z)

            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - ghost.rot))
            poseStack.scale(-1.0f, -1.0f, 1.0f)
            poseStack.translate(0.0, -1.501, 0.0)

            val model = if (ghost.skin.model == PlayerModelType.SLIM) slimModel else wideModel
            val renderState = AvatarRenderState()
            model.setupAnim(renderState)
            val renderType = RenderTypes.entityTranslucent(ghost.skin.body.texturePath())
            val vertexConsumer = bufferSource.getBuffer(renderType)
            model.renderToBuffer(
                poseStack,
                vertexConsumer,
                15728880,
                OverlayTexture.NO_OVERLAY,
                ColorUtil.rgba(0xffffff80)
            )

            poseStack.popPose()
        }
    }
}