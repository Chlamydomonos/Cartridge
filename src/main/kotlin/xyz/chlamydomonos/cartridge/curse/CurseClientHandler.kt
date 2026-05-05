package xyz.chlamydomonos.cartridge.curse

import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent
import xyz.chlamydomonos.cartridge.loaders.EffectLoader
import xyz.chlamydomonos.cartridge.utils.RLUtil
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

    object ExplosionHandler {

        private fun spawnItemBurstParticles(entity: Entity, itemStack: ItemStack, count: Int, averageSpeed: Double) {
            if (itemStack.isEmpty) return
            val level = entity.level()
            val random = entity.random

            val particleOption = ItemParticleOption(
                ParticleTypes.ITEM,
                ItemStackTemplate.fromNonEmptyStack(itemStack)
            )

            val centerX = entity.x
            val centerY = entity.y + (entity.bbHeight / 2.0)
            val centerZ = entity.z
            repeat(count) {
                val spawnX = centerX + (random.nextFloat() - 0.5) * entity.bbWidth
                val spawnY = centerY + (random.nextFloat() - 0.5) * entity.bbHeight
                val spawnZ = centerZ + (random.nextFloat() - 0.5) * entity.bbWidth

                val dirX = random.nextFloat() - 0.5
                val dirY = random.nextFloat() - 0.5
                val dirZ = random.nextFloat() - 0.5

                var direction = Vec3(dirX, dirY, dirZ)

                direction = if (direction.lengthSqr() > 0) {
                    direction.normalize()
                } else {
                    Vec3(0.0, 1.0, 0.0) // 默认向上
                }

                val speedMultiplier = averageSpeed * (0.5 + random.nextFloat())
                val speedX = direction.x * speedMultiplier
                val speedY = direction.y * speedMultiplier
                val speedZ = direction.z * speedMultiplier

                level.addParticle(
                    particleOption,
                    spawnX, spawnY, spawnZ,
                    speedX, speedY, speedZ
                )
            }
        }

        val explodingEntities = mutableMapOf<UUID, Int>()

        fun run() {
            val level = Minecraft.getInstance().level ?: return
            val iterator = explodingEntities.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val entity = level.getEntity(entry.key)
                if (entity !is Player) {
                    iterator.remove()
                    continue
                }

                spawnItemBurstParticles(entity, ItemStack(Items.PINK_WOOL), 80, 1.0)
                spawnItemBurstParticles(entity, ItemStack(Items.MAGENTA_WOOL), 50, 1.0)
                spawnItemBurstParticles(entity, ItemStack(Items.REDSTONE), 50, 1.1)
                level.playLocalSound(entity, SoundEvents.SLIME_BLOCK_BREAK, SoundSource.PLAYERS, 3f, 1f)
                level.playLocalSound(entity, SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 3f, 1f)
                val newValue = entry.value - 1
                if (newValue <= 0) {
                    iterator.remove()
                    continue
                }
                entry.setValue(newValue)
            }
        }
    }

    @SubscribeEvent
    fun onClientTick(@Suppress("unused") event: ClientTickEvent.Post) {
        PostEffectHandler.run()
        VomitHandler.run()
        BloodHandler.run()
        ExplosionHandler.run()
    }

    @SubscribeEvent
    fun onMovementInputUpdate(event: MovementInputUpdateEvent) {
        ConfusionHandler.run(event)
    }
}