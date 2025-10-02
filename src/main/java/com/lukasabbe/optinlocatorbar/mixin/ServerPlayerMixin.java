package com.lukasabbe.optinlocatorbar.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.lukasabbe.optinlocatorbar.LocatorBarOptInAttachedData;
import com.lukasabbe.optinlocatorbar.ModAttachmentTypes;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends PlayerEntity{

    public ServerPlayerMixin(World world, GameProfile profile) { super(world, profile); }

    @Shadow @Final private static EntityAttributeModifier WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER;

    @Shadow public abstract ServerWorld getEntityWorld();

    @Inject(
            method = "updateCreativeInteractionRangeModifiers",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeInstance;removeModifier(Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V", ordinal = 2),
            cancellable = true)
    void optInLocatorBar(CallbackInfo ci, @Local(ordinal = 2) EntityAttributeInstance entityAttributeInstance){
        LocatorBarOptInAttachedData data = this.getEntityWorld().getAttachedOrCreate(ModAttachmentTypes.LOCATOR_BAR_OPT_IN);
        if(!data.getValue(this.uuid)){
            entityAttributeInstance.updateModifier(WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER);
            ci.cancel();
        }
    }
}
