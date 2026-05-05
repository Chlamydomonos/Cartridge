package xyz.chlamydomonos.catridge.loaders.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.data.tags.DamageTypeTagsProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.DamageTypeTags
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
                        0.3f
                    ))
                }
        )

        event.createProvider { output, lp ->
            object : DamageTypeTagsProvider(output, lp, Catridge.ID) {
                override fun addTags(registries: HolderLookup.Provider) {
                    tag(DamageTypeTags.NO_KNOCKBACK).add(CURSE)
                    tag(DamageTypeTags.BYPASSES_ARMOR).add(CURSE)
                    tag(DamageTypeTags.BYPASSES_INVULNERABILITY).add(CURSE)
                }
            }
        }
    }
}