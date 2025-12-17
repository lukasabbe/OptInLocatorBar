package com.lukasabbe.optinlocatorbar;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class OptInLocatorBar implements DedicatedServerModInitializer {
    public static final String MOD_ID = "optinlocatorbar";
    @Override
    public void onInitializeServer() {
        ModAttachmentTypes.init();

        CommandRegistrationCallback.EVENT.register(
                ((commandDispatcher, commandRegistryAccess, registrationEnvironment) ->  {
            commandDispatcher.register(
                    Commands.
                            literal("locatorbar")
                            .requires(CommandSourceStack::isPlayer)
                            .then(Commands.literal("enable").executes(ctx -> runCommand(ctx, true)))
                            .then(Commands.literal("disable").executes(ctx -> runCommand(ctx, false)))
                            .executes(this::statusCommand)
            );
        }));
    }

    private int statusCommand(CommandContext<CommandSourceStack> serverCommandSourceCommandContext) {

        ServerPlayer player = serverCommandSourceCommandContext.getSource().getPlayer();

        if(player == null) return 0;

        Level world = serverCommandSourceCommandContext.getSource().getLevel();
        LocatorBarOptInAttachedData data = world.getAttachedOrCreate(ModAttachmentTypes.LOCATOR_BAR_OPT_IN);

        boolean isOpedIn;

        if(!data.playerOptInData().containsKey(player.getUUID().toString())){
            world.setAttached(ModAttachmentTypes.LOCATOR_BAR_OPT_IN, data.setValue(player.getUUID(), true));
            isOpedIn = true;
        }else{
            isOpedIn = data.playerOptInData().get(player.getUUID().toString());
        }

        if(isOpedIn) serverCommandSourceCommandContext.getSource().sendSystemMessage(Component.literal("You are visible to other players through the locator bar"));
        else serverCommandSourceCommandContext.getSource().sendSystemMessage(Component.literal("You are not visible to other players through the locator bar"));

        return 1;
    }

    private int runCommand(CommandContext<CommandSourceStack> serverCommandSourceCommandContext, boolean status) {
        ServerPlayer player = serverCommandSourceCommandContext.getSource().getPlayer();

        if(player == null) return 0;

        Level world = serverCommandSourceCommandContext.getSource().getLevel();
        LocatorBarOptInAttachedData data = world.getAttachedOrCreate(ModAttachmentTypes.LOCATOR_BAR_OPT_IN);

        world.setAttached(ModAttachmentTypes.LOCATOR_BAR_OPT_IN, data.setValue(player.getUUID(), status));

        if(status) serverCommandSourceCommandContext.getSource().sendSystemMessage(Component.literal("You are now visible to other players through the locator bar"));
        else serverCommandSourceCommandContext.getSource().sendSystemMessage(Component.literal("You are now not visible to other players through the locator bar"));

        return 1;
    }

}
