package xyz.chlamydomonos.cartridge.sparagmos

import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import top.theillusivec4.curios.api.CuriosApi
import xyz.chlamydomonos.cartridge.combat.CombatModeKeyMappings
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.inCombatMode
import kotlin.jvm.optionals.getOrNull

@EventBusSubscriber(value = [Dist.CLIENT])
object SparagmosInputHandler {
    val leftHandOffset = Vec3(-0.375, 0.75, 0.0)
    val rightHandOffset = Vec3(0.375, 0.75, 0.0)

    const val COOLDOWN = 20
    var mainHandCooldown = 0
    var offHandCooldown = 0

    fun hasSparagmos(player: Player, isMainHand: Boolean): Boolean {
        val inventory = CuriosApi.getCuriosInventory(player).getOrNull() ?: return false
        val stacks = inventory.getStacksHandler("bracelet").getOrNull()?.stacks ?: return false
        var count = 0
        for (i in 0..<stacks.slots) {
            val stack = stacks.getStackInSlot(i)
            if (stack.`is`(ItemLoader.SPARAGMOS)) {
                count++
            }
        }
        return count >= (if (isMainHand) 1 else 2)
    }

    fun handleKey(isMainHand: Boolean, player: Player) {
        if (!hasSparagmos(player, isMainHand)) {
            return
        }

        val cooldown = if (isMainHand) mainHandCooldown else offHandCooldown
        if (cooldown > 0) {
            return
        }

        if (isMainHand) {
            mainHandCooldown = COOLDOWN
        } else {
            offHandCooldown = COOLDOWN
        }

        val isLeftHand = isMainHand xor (player.mainArm == HumanoidArm.RIGHT)
        val handOffset = if (isLeftHand) leftHandOffset else rightHandOffset

        val yRot = player.yRot
        val yRotQuaternion = Axis.YP.rotationDegrees(180 - yRot)
        val realHandOffset = handOffset.toVector3f().rotate(yRotQuaternion)
        val handPos = player.position().toVector3f().add(realHandOffset)

        ClientPacketDistributor.sendToServer(
            SparagmosInputPacket(
                handPos,
                player.xRot,
                player.yRot,
            )
        )
    }

    @SubscribeEvent
    fun onClientTick(@Suppress("unused") event: ClientTickEvent.Post) {
        if (mainHandCooldown > 0) {
            mainHandCooldown--
        }

        if (offHandCooldown > 0) {
            offHandCooldown--
        }

        val player = Minecraft.getInstance().player ?: return

        if (player.inCombatMode) {
            while (CombatModeKeyMappings.MAIN_HAND_ATTACK.consumeClick()) {
                handleKey(true, player)
            }

            while (CombatModeKeyMappings.OFF_HAND_ATTACK.consumeClick()) {
                handleKey(false, player)
            }
        }
    }
}