package com.lukasabbe.optinlocatorbar.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.lukasabbe.optinlocatorbar.LocatorBarOptInAttachedData;
import com.lukasabbe.optinlocatorbar.ModAttachmentTypes;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    public ServerPlayerMixin(Level world, GameProfile profile) { super(world, profile); }

    @Shadow @Final private static AttributeModifier WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER;

    @Shadow
    public abstract ServerLevel level();

    @Inject(
            method = "updatePlayerAttributes",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;removeModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V", ordinal = 2),
            cancellable = true)
    void optInLocatorBar(CallbackInfo ci, @Local(name = "waypointTransmitRange") AttributeInstance entityAttributeInstance){
        LocatorBarOptInAttachedData data = this.level().getAttachedOrCreate(ModAttachmentTypes.LOCATOR_BAR_OPT_IN);
        if(!data.getValue(this.uuid)){
            entityAttributeInstance.addOrUpdateTransientModifier(WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER);
            ci.cancel();
        }
    }
}
