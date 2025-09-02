package com.cravencraft.stamina.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "canDisableShield", at = @At(value = "RETURN"), cancellable = true)
    public void axeCannotDisableShield(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
