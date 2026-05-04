package xyz.chlamydomonos.catridge.loaders.datagen

import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageScaling
import net.minecraft.world.damagesource.DamageType
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import xyz.chlamydomonos.catridge.Catridge
import xyz.chlamydomonos.catridge.utils.RLUtil

@EventBusSubscriber
object DamageTypeLoader {
    val CURSE = ResourceKey.create(Registries.DAMAGE_TYPE, RLUtil.of("curse"))

    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createDatapackRegistryObjects(
            RegistrySetBuilder()
                .add(Registries.DAMAGE_TYPE) {
                    it.register(CURSE, DamageType(
                        "${Catridge.ID}.curse",
                        DamageScaling.NEVER,
                        0.1f
                    ))
                }
        )
    }
}