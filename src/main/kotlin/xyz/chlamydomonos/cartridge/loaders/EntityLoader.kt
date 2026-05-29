package xyz.chlamydomonos.cartridge.loaders

import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.ai.attributes.Attributes
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.cartridge.CartridgeEntity
import xyz.chlamydomonos.cartridge.hollow.CarriedHollowEntity
import xyz.chlamydomonos.cartridge.hollow.HollowEntity

@EventBusSubscriber
object EntityLoader {
    private val registry = DeferredRegister.createEntities(Cartridge.ID)

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }

    val HOLLOW by registry.registerEntityType("hollow", ::HollowEntity, MobCategory.MISC) {
        it.sized(1f, 0.5f).eyeHeight(0.4f).noLootTable()
    }

    val CARTRIDGE by registry.registerEntityType("cartridge", ::CartridgeEntity, MobCategory.MISC) {
        it.sized(0f, 0f).fireImmune().noLootTable().noSummon()
    }

    val CARRIED_HOLLOW by registry.registerEntityType("carried_hollow", ::CarriedHollowEntity, MobCategory.MISC) {
        it.sized(0f, 0f).fireImmune().noLootTable().noSummon()
    }

    @SubscribeEvent
    fun onCreateAttributes(event: EntityAttributeCreationEvent) {
        event.put(
            HOLLOW,
            Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .build()
        )
    }
}