package xyz.chlamydomonos.cartridge.gangway

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
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
object GangwayInputHandler {
    fun hasGangway(player: Player): Boolean {
        val inventory = CuriosApi.getCuriosInventory(player).getOrNull() ?: return false
        @Suppress("removal", "DEPRECATION")
        return inventory.equippedCurios.let { equipped ->
            for (i in 0..<equipped.slots) {
                if (equipped.getStackInSlot(i).`is`(ItemLoader.GANGWAY)) {
                    return true
                }
            }
            false
        }
    }

    const val LIGHT_ATTACK_COOLDOWN = 10
    const val HEAVY_ATTACK_COOLDOWN = 20
    const val HEAVY_ATTACK_THRESHOLD = 5

    var cooldown = 0
    var holdTime = 0

    @SubscribeEvent
    fun onClientTick(@Suppress("Unused") event: ClientTickEvent.Post) {
        if (cooldown > 0) {
            cooldown--
        }

        val player = Minecraft.getInstance().player ?: return
        if (!player.inCombatMode) {
            return
        }

        if (!hasGangway(player)) {
            return
        }

        if (CombatModeKeyMappings.GANGWAY.isDown) {
            if (cooldown == 0 && holdTime < HEAVY_ATTACK_THRESHOLD) {
                holdTime++
            }
            return
        }

        if (holdTime > 0) {
            val isHeavyAttack = holdTime == HEAVY_ATTACK_THRESHOLD

            ClientPacketDistributor.sendToServer(
                GangwayInputPacket(
                    player.eyePosition.toVector3f(),
                    player.getViewVector(1f).toVector3f(),
                    isHeavyAttack
                )
            )

            cooldown = if (isHeavyAttack) HEAVY_ATTACK_COOLDOWN else LIGHT_ATTACK_COOLDOWN
        }

        holdTime = 0
    }
}
