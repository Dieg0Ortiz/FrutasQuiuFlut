package com.quiufuits.fruits;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Efectos por etapa de cosecha. Duraciones en ticks (20 ticks = 1 s).
 */
public record FruitEffectProfile(
		MobEffect underRipeEffect,
		int underRipeDuration,
		int underRipeAmplifier,
		MobEffect ripeEffect,
		int ripeDuration,
		int ripeAmplifier,
		MobEffect rottenEffect,
		int rottenDuration,
		int rottenAmplifier
) {
	public MobEffectInstance underRipeInstance() {
		return underRipeEffect == null ? null : new MobEffectInstance(underRipeEffect, underRipeDuration, underRipeAmplifier);
	}

	public MobEffectInstance ripeInstance() {
		return ripeEffect == null ? null : new MobEffectInstance(ripeEffect, ripeDuration, ripeAmplifier);
	}

	public MobEffectInstance rottenInstance() {
		return rottenEffect == null ? null : new MobEffectInstance(rottenEffect, rottenDuration, rottenAmplifier);
	}

	public static FruitEffectProfile of(
			MobEffect weak,
			int weakDuration,
			int weakAmp,
			MobEffect full,
			int fullDuration,
			int fullAmp,
			MobEffect negative,
			int negDuration,
			int negAmp
	) {
		return new FruitEffectProfile(weak, weakDuration, weakAmp, full, fullDuration, fullAmp, negative, negDuration, negAmp);
	}
}
