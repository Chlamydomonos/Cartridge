package xyz.chlamydomonos.cartridge.hollow

import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import xyz.chlamydomonos.cartridge.curse.CurseEffect

@EventBusSubscriber
object HollowCommand {
    val notPlayer = SimpleCommandExceptionType(Component.translatable("error.cartridge.not_player"))

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.dispatcher.register(
            Commands.literal("hollow")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes { context ->
                    val player = context.source.player ?: throw CommandSyntaxException(
                        notPlayer,
                        Component.empty()
                    )

                    CurseEffect.turnToHollow(player)
                    context.source.sendSuccess(
                        { Component.translatable("command.cartridge.hollow") },
                        false
                    )
                    1
                }
                .then(
                    Commands.argument("targets", EntityArgument.players())
                        .executes { context ->
                            val sourcePlayer = context.source.player
                            val targets = EntityArgument.getPlayers(context, "targets")

                            val result = targets.count()

                            context.source.sendSuccess(
                                {
                                    Component.translatable("command.cartridge.hollow.source", result)
                                },
                                false
                            )

                            for (target in targets) {
                                target.sendSystemMessage(
                                    if (sourcePlayer != null) {
                                        Component.translatable(
                                            "command.cartridge.hollow.player",
                                            sourcePlayer.displayName
                                        )
                                    } else {
                                        Component.translatable("command.cartridge.hollow.block")
                                    }
                                )
                                CurseEffect.turnToHollow(target)
                            }

                            result
                        }
                )
        )
    }
}