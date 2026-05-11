package xyz.chlamydomonos.cartridge;

import net.minecraft.world.damagesource.DeathMessageType;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import xyz.chlamydomonos.cartridge.cartridge.CartridgeDeathMessageProvider;

@SuppressWarnings("unused")
public class EnumExtensionHandler {
    public static EnumProxy<DeathMessageType> CARTRIDGE_DEATH_MESSAGE_TYPE_PROXY = CartridgeDeathMessageProvider.INSTANCE.getDEATH_MESSAGE_TYPE_PROXY();
}
