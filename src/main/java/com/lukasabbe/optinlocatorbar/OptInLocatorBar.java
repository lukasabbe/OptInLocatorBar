package com.lukasabbe.optinlocatorbar;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class OptInLocatorBar implements DedicatedServerModInitializer {
    public static final String MOD_ID = "optinlocatorbar";
    @Override
    public void onInitializeServer() {
        ModAttachmentTypes.init();

        CommandRegistrationCallback.EVENT.register(
                ((commandDispatcher, commandRegistryAccess, registrationEnvironment) ->  {
            commandDispatcher.register(
                    CommandManager.
                            literal("locatorbar")
                            .requires(ServerCommandSource::isExecutedByPlayer)
                            .then(CommandManager.literal("enable").executes(ctx -> runCommand(ctx, true)))
                            .then(CommandManager.literal("disable").executes(ctx -> runCommand(ctx, false)))
                            .executes(this::statusCommand)
            );
        }));
    }

    private int statusCommand(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {

        ServerPlayerEntity player = serverCommandSourceCommandContext.getSource().getPlayer();

        if(player == null) return 0;

        ServerWorld world = serverCommandSourceCommandContext.getSource().getWorld();
        LocatorBarOptInAttachedData data = world.getAttachedOrCreate(ModAttachmentTypes.LOCATOR_BAR_OPT_IN);

        boolean isOpedIn;

        if(!data.playerOptInData().containsKey(player.getUuid().toString())){
            world.setAttached(ModAttachmentTypes.LOCATOR_BAR_OPT_IN, data.setValue(player.getUuid(), true));
            isOpedIn = true;
        }else{
            isOpedIn = data.playerOptInData().get(player.getUuid().toString());
        }

        if(isOpedIn) serverCommandSourceCommandContext.getSource().sendMessage(Text.literal("You are visible to other players through the locator bar"));
        else serverCommandSourceCommandContext.getSource().sendMessage(Text.literal("You are not visible to other players through the locator bar"));

        return 1;
    }

    private int runCommand(CommandContext<ServerCommandSource> serverCommandSourceCommandContext, boolean status) {
        ServerPlayerEntity player = serverCommandSourceCommandContext.getSource().getPlayer();

        if(player == null) return 0;

        ServerWorld world = serverCommandSourceCommandContext.getSource().getWorld();
        LocatorBarOptInAttachedData data = world.getAttachedOrCreate(ModAttachmentTypes.LOCATOR_BAR_OPT_IN);

        world.setAttached(ModAttachmentTypes.LOCATOR_BAR_OPT_IN, data.setValue(player.getUuid(), status));

        if(status) serverCommandSourceCommandContext.getSource().sendMessage(Text.literal("You are now visible to other players through the locator bar"));
        else serverCommandSourceCommandContext.getSource().sendMessage(Text.literal("You are now not visible to other players through the locator bar"));

        return 1;
    }

}
