package xyz.chlamydomonos.cartridge.abyss

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ShapeRenderer
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.joml.Matrix4fc
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import xyz.chlamydomonos.cartridge.utils.optionalBlockPos
import kotlin.math.max
import kotlin.math.min


@EventBusSubscriber(value = [Dist.CLIENT])
object AbyssRenderer {
    var root = OctreeNode()

    fun renderVoxelShape(shape: VoxelShape, cameraPos: Vec3, poseStack: PoseStack, color: Int) {
        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()
        val renderType = RenderTypes.lines()
        val vertexConsumer = bufferSource.getBuffer(renderType)

        poseStack.pushPose()

        ShapeRenderer.renderShape(
            poseStack,
            vertexConsumer,
            shape,
            -cameraPos.x,
            -cameraPos.y,
            -cameraPos.z,
            ColorUtil.rgbAsInt(color),
            4f
        )

        poseStack.popPose()
    }

    fun renderTransparentAABB(aabb: AABB, matrix: Matrix4fc, color: Int, builder: VertexConsumer) {
        val minX = aabb.minX.toFloat()
        val minY = aabb.minY.toFloat()
        val minZ = aabb.minZ.toFloat()
        val maxX = aabb.maxX.toFloat()
        val maxY = aabb.maxY.toFloat()
        val maxZ = aabb.maxZ.toFloat()

        builder.addVertex(matrix, minX, minY, minZ).setColor(color)
        builder.addVertex(matrix, maxX, minY, minZ).setColor(color)
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(color)
        builder.addVertex(matrix, minX, minY, maxZ).setColor(color)

        builder.addVertex(matrix, minX, maxY, minZ).setColor(color)
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(color)
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(color)
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(color)

        builder.addVertex(matrix, minX, minY, minZ).setColor(color)
        builder.addVertex(matrix, minX, maxY, minZ).setColor(color)
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(color)
        builder.addVertex(matrix, maxX, minY, minZ).setColor(color)

        builder.addVertex(matrix, minX, minY, maxZ).setColor(color)
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(color)
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(color)
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(color)

        builder.addVertex(matrix, minX, minY, minZ).setColor(color)
        builder.addVertex(matrix, minX, minY, maxZ).setColor(color)
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(color)
        builder.addVertex(matrix, minX, maxY, minZ).setColor(color)

        builder.addVertex(matrix, maxX, minY, minZ).setColor(color)
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(color)
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(color)
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(color)
    }

    fun renderTransparentShape(shape: VoxelShape, cameraPos: Vec3, poseStack: PoseStack, color: Int) {
        poseStack.pushPose()
        poseStack.translate(cameraPos.scale(-1.0))

        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()
        val buffer = bufferSource.getBuffer(RenderTypes.debugFilledBox())
        val matrix = poseStack.last().pose()
        shape.forAllBoxes { x1, y1, z1, x2, y2, z2 ->
            renderTransparentAABB(
                AABB(x1, y1, z1, x2, y2, z2),
                matrix,
                color,
                buffer
            )
        }

        poseStack.popPose()
    }

    @SubscribeEvent
    fun onRenderLevel(event: RenderLevelStageEvent.AfterTranslucentBlocks) {
        val player = Minecraft.getInstance().player ?: return
        val stack = player.mainHandItem
        val item = stack.item
        if (item !is AbyssEditToolItem) {
            return
        }

        val abyssLevel = item.level
        val color = if (item.operation == AbyssEditToolItem.Operation.ADD) {
            0x80ff80
        } else {
            0xff8080
        }
        val abyssShape = root.getVoxelShape(player.blockPosition(), 100, abyssLevel.toByte())
        val cameraPos = event.levelRenderState.cameraRenderState.pos
        renderVoxelShape(abyssShape, cameraPos, event.poseStack, 0xffffff)
        renderTransparentShape(abyssShape, cameraPos, event.poseStack, ColorUtil.rgbaAsInt(0xffffff, 0x80))

        val chosenPos = stack.optionalBlockPos ?: return
        val hitResult = Minecraft.getInstance().hitResult ?: return
        if (hitResult.type != HitResult.Type.BLOCK || hitResult !is BlockHitResult) {
            return
        }
        val hitPos = hitResult.blockPos

        val minX = min(chosenPos.x.toDouble(), hitPos.x.toDouble())
        val minY = min(chosenPos.y.toDouble(), hitPos.y.toDouble())
        val minZ = min(chosenPos.z.toDouble(), hitPos.z.toDouble())
        val maxX = max(chosenPos.x.toDouble(), hitPos.x.toDouble()) + 1
        val maxY = max(chosenPos.y.toDouble(), hitPos.y.toDouble()) + 1
        val maxZ = max(chosenPos.z.toDouble(), hitPos.z.toDouble()) + 1

        val tempShape = Shapes.create(AABB(minX, minY, minZ, maxX, maxY, maxZ))
        renderVoxelShape(tempShape, cameraPos, event.poseStack, color)
        renderTransparentShape(tempShape, cameraPos, event.poseStack, ColorUtil.rgbaAsInt(color, 0x80))
    }
}