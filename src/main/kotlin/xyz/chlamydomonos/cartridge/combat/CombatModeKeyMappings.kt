package xyz.chlamydomonos.cartridge.combat

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.settings.IKeyConflictContext
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.client.settings.KeyModifier
import org.lwjgl.glfw.GLFW
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.utils.RLUtil

@EventBusSubscriber(value = [Dist.CLIENT])
object CombatModeKeyMappings {
    val category = KeyMapping.Category(RLUtil.of("category"))

    val keyMappings = mutableListOf<Lazy<KeyMapping>>()

    fun register(value: () -> KeyMapping): Lazy<KeyMapping> {
        val delegate = lazy(value)
        keyMappings.add(delegate)
        return delegate
    }

    fun register(
        name: String,
        context: IKeyConflictContext,
        type: InputConstants.Type,
        key: Int
    ): Lazy<KeyMapping> {
        return register {
            KeyMapping(
                "key.${Cartridge.ID}.$name",
                context,
                type,
                key,
                category
            )
        }
    }

    fun register(
        name: String,
        context: IKeyConflictContext,
        modifier: KeyModifier,
        type: InputConstants.Type,
        key: Int
    ): Lazy<KeyMapping> {
        return register {
            KeyMapping(
                "key.${Cartridge.ID}.$name",
                context,
                modifier,
                type,
                key,
                category
            )
        }
    }

    @SubscribeEvent
    fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        for (delegate in keyMappings) {
            event.register(delegate.value)
        }
    }

    val TOGGLE_COMBAT_MODE by register(
        "toggle_combat_mode",
        KeyConflictContext.IN_GAME,
        KeyModifier.CONTROL,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R
    )

    val EJECT_CARTRIDGE by register(
        "eject_cartridge",
        CombatModeContext,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R
    )

    val MAIN_HAND_ATTACK by register(
        "main_hand_attack",
        CombatModeContext,
        InputConstants.Type.MOUSE,
        GLFW.GLFW_MOUSE_BUTTON_LEFT
    )

    val OFF_HAND_ATTACK by register(
        "off_hand_attack",
        CombatModeContext,
        InputConstants.Type.MOUSE,
        GLFW.GLFW_MOUSE_BUTTON_RIGHT
    )
}