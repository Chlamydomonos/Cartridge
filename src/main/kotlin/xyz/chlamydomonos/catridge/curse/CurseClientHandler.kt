package xyz.chlamydomonos.catridge.curse

import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent
import xyz.chlamydomonos.catridge.loaders.EffectLoader
import xyz.chlamydomonos.catridge.utils.RLUtil
import java.util.*

@EventBusSubscriber(value = [Dist.CLIENT])
object CurseClientHandler {
    object PostEffectHandler {
        fun getPostEffectId(level: Int): Identifier? {
            return when (level) {
                1 -> RLUtil.of("curse_1")
                2 -> RLUtil.of("curse_2")
                3 -> RLUtil.of("curse_3")
                4 -> RLUtil.of("curse_4")
                5 -> RLUtil.of("curse_5")
                else -> null
            }
        }

        fun run() {
            val mc = Minecraft.getInstance()
            val player = mc.player
            val renderer = mc.gameRenderer
            if (player == null) {
                if (renderer.currentPostEffect() != null) {
                    renderer.clearPostEffect()
                }
                return
            }

            if (player.isSpectator) {
                return
            }

            val effectLevel = player.getEffect(EffectLoader.CURSE)?.amplifier?.plus(1) ?: 0
            val postEffect = getPostEffectId(effectLevel)
            if (renderer.currentPostEffect() != postEffect) {
                if (postEffect == null) {
                    renderer.clearPostEffect()
                    return
                }
                renderer.setPostEffect(postEffect)
            }
        }
    }

    object VomitHandler {
        val vomitingEntities = mutableMapOf<UUID, Int>()
        fun run() {
            val level = Minecraft.getInstance().level ?: return
            val iterator = vomitingEntities.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val entity = level.getEntity(entry.key)
                if (entity !is Player) {
                    iterator.remove()
                    continue
                }

                entity.spawnItemParticles(ItemStack(Items.POISONOUS_POTATO), 20)
                entity.spawnItemParticles(ItemStack(Items.ROTTEN_FLESH), 10)
                val newValue = entry.value - 1
                if (newValue <= 0) {
                    iterator.remove()
                    continue
                }
                entry.setValue(newValue)
            }
        }
    }

    object BloodHandler {
        val bleedingEntities = mutableMapOf<UUID, Int>()
        fun run() {
            val level = Minecraft.getInstance().level ?: return
            val iterator = bleedingEntities.entries.iterator()
            val random = level.random
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val entity = level.getEntity(entry.key)
                if (entity !is Player) {
                    iterator.remove()
                    continue
                }

                entity.spawnItemParticles(ItemStack(Items.REDSTONE), random.nextInt(20) - 10)
                val newValue = entry.value - 1
                if (newValue <= 0) {
                    iterator.remove()
                    continue
                }
                entry.setValue(newValue)
            }
        }
    }

    object ConfusionHandler {
        var confusionTime = 0
        fun run(event: MovementInputUpdateEvent) {
            if (confusionTime <= 0) return
            confusionTime--
            val input = event.input
            input.moveVector = input.moveVector.negated()
        }
    }

    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        PostEffectHandler.run()
        VomitHandler.run()
        BloodHandler.run()
    }

    @SubscribeEvent
    fun onMovementInputUpdate(event: MovementInputUpdateEvent) {
        ConfusionHandler.run(event)
    }
}