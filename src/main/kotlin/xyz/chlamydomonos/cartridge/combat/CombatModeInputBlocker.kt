package xyz.chlamydomonos.cartridge.combat

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.InputEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.utils.inCombatMode
import kotlin.reflect.KProperty

@EventBusSubscriber(value = [Dist.CLIENT])
object CombatModeInputBlocker {
    fun genKeyMappings(): Map<InputConstants.Key, MutableList<KeyMapping>> {
        val otherModMappings = Minecraft.getInstance().options.keyMappings.filter {
            it.category != CombatModeKeyMappings.category
        }

        val thisModMappings = Minecraft.getInstance().options.keyMappings.filter {
            it.category == CombatModeKeyMappings.category
        }

        val thisModMap = mutableMapOf<InputConstants.Key, MutableList<KeyMapping>>()
        for (mapping in thisModMappings) {
            val value = thisModMap.getOrPut(mapping.key) { mutableListOf() }
            value.add(mapping)
        }

        val result = thisModMap.keys.associateWith { mutableListOf<KeyMapping>() }
        for (mapping in otherModMappings) {
            result[mapping.key]?.add(mapping)
        }

        return result
    }

    class Delegate {
        var value: Map<InputConstants.Key, MutableList<KeyMapping>>? = null
        operator fun getValue(
            thisRef: Any?,
            propertyKey: KProperty<*>
        ): Map<InputConstants.Key, MutableList<KeyMapping>> {
            return value ?: Unit.let {
                val newValue = genKeyMappings()
                value = newValue
                newValue
            }
        }

        fun update() {
            value = genKeyMappings()
        }
    }

    val delegate = Delegate()

    val toBlock by delegate

    val pressed = mutableSetOf<KeyMapping>()

    @SubscribeEvent
    fun onMouseInput(event: InputEvent.MouseButton.Pre) {
        if (!(Minecraft.getInstance().player?.inCombatMode ?: false)) {
            return
        }

        val key = InputConstants.Type.MOUSE.getOrCreate(event.button)
        toBlock[key]?.let { pressed.addAll(it) }
    }

    @SubscribeEvent
    fun onInteract(event: InputEvent.InteractionKeyMappingTriggered) {
        if (!(Minecraft.getInstance().player?.inCombatMode ?: false)) {
            return
        }

        val key = event.keyMapping.key

        if (toBlock.containsKey(key)) {
            event.isCanceled = true
            Cartridge.logger.debug("blocked interact input: {}", event.keyMapping.name)
            event.setSwingHand(false)
        }
    }

    @SubscribeEvent
    fun onKeyboardInput(event: InputEvent.Key) {
        if (!(Minecraft.getInstance().player?.inCombatMode ?: false)) {
            return
        }

        val key = InputConstants.Type.KEYSYM.getOrCreate(event.key)
        toBlock[key]?.let { pressed.addAll(it) }
    }

    @SubscribeEvent
    fun preClientTick(@Suppress("unused") event: ClientTickEvent.Pre) {
        if (!(Minecraft.getInstance().player?.inCombatMode ?: false)) {
            return
        }

        for (key in pressed) {
            while (key.consumeClick()) {
                Cartridge.logger.debug("blocked input: {}", key.name)
            }
        }

        pressed.clear()
    }

    @SubscribeEvent
    fun postClientTick(@Suppress("unused") event: ClientTickEvent.Post) {
        val player = Minecraft.getInstance().player ?: return
        while (CombatModeKeyMappings.TOGGLE_COMBAT_MODE.consumeClick()) {
            player.inCombatMode = !player.inCombatMode
            if (player.inCombatMode) {
                player.sendOverlayMessage(
                    Component.translatable(
                        "message.cartridge.exit_combat_mode",
                        CombatModeKeyMappings.TOGGLE_COMBAT_MODE.translatedKeyMessage
                    )
                )
            }
        }
    }

    fun onUpdateKeyMappings() {
        delegate.update()
    }
}