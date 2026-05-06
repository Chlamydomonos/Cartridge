package xyz.chlamydomonos.cartridge.hollow

import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent

@EventBusSubscriber
object HollowEntityEventListener {
    @SubscribeEvent
    fun onEntityJoin(event: EntityJoinLevelEvent) {
        val entity = event.entity
        if (entity is Enemy && entity is PathfinderMob) {
            entity.targetSelector.addGoal(
                3,
                NearestAttackableTargetGoal(entity, HollowEntity::class.java, true)
            )
        }
    }
}