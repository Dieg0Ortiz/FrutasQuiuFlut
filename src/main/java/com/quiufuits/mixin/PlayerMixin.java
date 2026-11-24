package com.quiufuits.mixin;

import com.quiufuits.fruits.QuiuKeepInventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Fruto Quiu: al morir protegido no se suelta el inventario ni la experiencia. */
@Mixin(Player.class)
public abstract class PlayerMixin {
	@Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
	private void quiufuits$keepInventory(CallbackInfo ci) {
		Player player = (Player) (Object) this;
		if (!player.level().isClientSide && QuiuKeepInventory.keepOnDeath(player)) {
			ci.cancel();
		}
	}

	// dropEquipment corre antes que la experiencia, así que la marca ya está puesta
	@Inject(method = "getExperienceReward", at = @At("HEAD"), cancellable = true)
	private void quiufuits$keepExperience(CallbackInfoReturnable<Integer> cir) {
		if (QuiuKeepInventory.keptOnDeath((Player) (Object) this)) {
			cir.setReturnValue(0);
		}
	}
}
