package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.data.tags.DamageTypeTagsProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.damagesource.DamageEffects
import net.minecraft.world.damagesource.DamageScaling
import net.minecraft.world.damagesource.DamageType
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.cartridge.CartridgeDeathMessageProvider
import xyz.chlamydomonos.cartridge.utils.RLUtil

@EventBusSubscriber
object DamageTypeLoader {
    val CURSE = ResourceKey.create(Registries.DAMAGE_TYPE, RLUtil.of("curse"))
    val CARTRIDGE = ResourceKey.create(Registries.DAMAGE_TYPE, RLUtil.of("cartridge"))
    val CURSE_SIDE_EFFECT = ResourceKey.create(Registries.DAMAGE_TYPE, RLUtil.of("curse_side_effect"))
    val SPARAGMOS = ResourceKey.create(Registries.DAMAGE_TYPE, RLUtil.of("sparagmos"))
    val GANGWAY = ResourceKey.create(Registries.DAMAGE_TYPE, RLUtil.of("gangway"))

    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createDatapackRegistryObjects(
            RegistrySetBuilder()
                .add(Registries.DAMAGE_TYPE) {
                    it.register(CURSE, DamageType(
                        "${Cartridge.ID}.curse",
                        DamageScaling.NEVER,
                        0.3f
                    ))
                    it.register(CARTRIDGE, DamageType(
                        "${Cartridge.ID}.cartridge",
                        DamageScaling.NEVER,
                        0f,
                        DamageEffects.HURT,
                        CartridgeDeathMessageProvider.DEATH_MESSAGE_TYPE
                    ))
                    it.register(CURSE_SIDE_EFFECT, DamageType(
                        "${Cartridge.ID}.curse_side_effect",
                        DamageScaling.NEVER,
                        0.3f
                    ))
                    it.register(SPARAGMOS, DamageType(
                        "${Cartridge.ID}.sparagmos",
                        DamageScaling.NEVER,
                        0f,
                        DamageEffects.HURT,
                        CartridgeDeathMessageProvider.DEATH_MESSAGE_TYPE
                    ))
                    it.register(GANGWAY, DamageType(
                        "${Cartridge.ID}.gangway",
                        DamageScaling.NEVER,
                        0f,
                        DamageEffects.HURT,
                        CartridgeDeathMessageProvider.DEATH_MESSAGE_TYPE
                    ))
                }
        )

        event.createProvider { output, lp ->
            object : DamageTypeTagsProvider(output, lp, Cartridge.ID) {
                override fun addTags(registries: HolderLookup.Provider) {
                    tag(DamageTypeTags.NO_KNOCKBACK)
                        .add(CURSE)

                    tag(DamageTypeTags.BYPASSES_ARMOR)
                        .add(CURSE)
                        .add(CARTRIDGE)
                        .add(SPARAGMOS)

                    tag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                        .add(CURSE)
                        .add(CARTRIDGE)

                    tag(DamageTypeTags.BYPASSES_COOLDOWN)
                        .add(GANGWAY)

                    tag(DamageTypeTags.BYPASSES_SHIELD)
                        .add(SPARAGMOS)

                    tag(DamageTypeTags.BYPASSES_EFFECTS)
                        .add(SPARAGMOS)

                    tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                        .add(SPARAGMOS)

                    tag(DamageTypeTags.BYPASSES_RESISTANCE)
                        .add(SPARAGMOS)

                    tag(DamageTypeTags.BYPASSES_WOLF_ARMOR)
                        .add(SPARAGMOS)
                }
            }
        }
    }
}