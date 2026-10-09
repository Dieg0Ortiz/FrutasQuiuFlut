package com.quiufuits.fruits;

import com.quiufuits.QuiuFruitsMod;
import com.quiufuits.blocks.fruit.FruitCropBlock;
import com.quiufuits.items.FruitHairItem;
import com.quiufuits.items.FruitItem;
import com.quiufuits.items.FruitSeedItem;
import com.quiufuits.registry.ModBlocks;
import com.quiufuits.registry.ModItems;
import com.quiufuits.registry.ModTags;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro central de fichas de fruta.
 *
 * <p><b>Fruta Solaria — efecto elegido: Fire Resistance (Resistencia al fuego)</b><br>
 * Tema solar: la fruta concentra calor del sol. En lugar de quemar al jugador,
 * le otorga protección térmica (Fire Resistance). Poco madura = corta;
 * madura = completa; podrida = Hambre (energía solar corrompida).
 */
public final class FruitDefinitions {
	private static final Map<String, FruitDefinition> BY_ID = new LinkedHashMap<>();

	public static FruitDefinition SOLARIA;
	public static FruitDefinition GLEBANOIDA;
	public static FruitDefinition LUNAR;
	public static FruitDefinition MAREA;
	public static FruitDefinition BRUMA;
	public static FruitDefinition AMBAR;

	private FruitDefinitions() {
	}

	public static void bootstrap() {
		SOLARIA = register(FruitDefinition.builder("solaria")
				.color(0xFFC94A)
				.effects(FruitEffectProfile.of(
						MobEffects.FIRE_RESISTANCE, 8 * 20, 0,
						MobEffects.FIRE_RESISTANCE, 45 * 20, 0,
						MobEffects.HUNGER, 12 * 20, 0
				))
				.plantableOn(() -> ModTags.SOLARIA_PLANTABLE_LOGS)
				.seed(true)
				.hair(true, "solar")
				.mobFeed(FruitDefinition.MobFeedSpec.fromConfig())
				.tooltips(
						"item.quiufuits.solaria.tooltip.seed",
						"item.quiufuits.solaria.tooltip.hair",
						"item.quiufuits.solaria.tooltip.mob"
				)
				.build());

		GLEBANOIDA = register(FruitDefinition.builder("glebanoida")
				.color(0x7BC67E)
				.effects(FruitEffectProfile.of(
						MobEffects.DIG_SPEED, 10 * 20, 0,
						MobEffects.DIG_SPEED, 40 * 20, 1,
						MobEffects.POISON, 8 * 20, 0
				))
				.plantableOn(() -> ModTags.FRUIT_PLANTABLE_LOGS)
				.seed(true)
				.hair(true, "glebanoide")
				.mobFeed(FruitDefinition.MobFeedSpec.of("minecraft:cow", "minecraft:leather", 0.40))
				.tooltips(
						"item.quiufuits.glebanoida.tooltip.seed",
						"item.quiufuits.glebanoida.tooltip.hair",
						"item.quiufuits.glebanoida.tooltip.mob"
				)
				.build());

		LUNAR = register(FruitDefinition.builder("lunar")
				.color(0xA8C4FF)
				.effects(FruitEffectProfile.of(
						MobEffects.NIGHT_VISION, 15 * 20, 0,
						MobEffects.NIGHT_VISION, 90 * 20, 0,
						MobEffects.BLINDNESS, 8 * 20, 0
				))
				.plantableOn(() -> ModTags.ABEDUL_LOGS)
				.seed(true)
				.hair(true, "lunar")
				.mobFeed(FruitDefinition.MobFeedSpec.of("minecraft:cat", "minecraft:string", 0.30))
				.tooltips(
						"item.quiufuits.lunar.tooltip.seed",
						"item.quiufuits.lunar.tooltip.hair",
						"item.quiufuits.lunar.tooltip.mob"
				)
				.build());

		MAREA = register(FruitDefinition.builder("marea")
				.color(0x3FA7D6)
				.effects(FruitEffectProfile.of(
						MobEffects.WATER_BREATHING, 20 * 20, 0,
						MobEffects.WATER_BREATHING, 90 * 20, 0,
						MobEffects.CONFUSION, 10 * 20, 0
				))
				.plantableOn(() -> ModTags.QUIU_OAK_LOGS)
				.seed(true)
				.hair(true, "marino")
				.mobFeed(FruitDefinition.MobFeedSpec.of("minecraft:turtle", "minecraft:seagrass", 0.45))
				.tooltips(
						"item.quiufuits.marea.tooltip.seed",
						"item.quiufuits.marea.tooltip.hair",
						"item.quiufuits.marea.tooltip.mob"
				)
				.build());

		BRUMA = register(FruitDefinition.builder("bruma")
				.color(0xC9C2D8)
				.effects(FruitEffectProfile.of(
						MobEffects.SLOW_FALLING, 12 * 20, 0,
						MobEffects.SLOW_FALLING, 45 * 20, 0,
						MobEffects.MOVEMENT_SLOWDOWN, 12 * 20, 0
				))
				.plantableOn(() -> ModTags.FRUIT_PLANTABLE_LOGS)
				.seed(true)
				.hair(true, "bruma")
				.mobFeed(FruitDefinition.MobFeedSpec.of("minecraft:phantom", "minecraft:phantom_membrane", 0.25))
				.tooltips(
						"item.quiufuits.bruma.tooltip.seed",
						"item.quiufuits.bruma.tooltip.hair",
						"item.quiufuits.bruma.tooltip.mob"
				)
				.build());

		AMBAR = register(FruitDefinition.builder("ambar")
				.color(0xE0A100)
				.effects(FruitEffectProfile.of(
						MobEffects.DAMAGE_RESISTANCE, 10 * 20, 0,
						MobEffects.DAMAGE_RESISTANCE, 35 * 20, 0,
						MobEffects.WEAKNESS, 15 * 20, 0
				))
				.plantableOn(() -> ModTags.QUIU_OAK_LOGS)
				.seed(true)
				.hair(true, "ambarino")
				.mobFeed(FruitDefinition.MobFeedSpec.of("minecraft:bee", "minecraft:honeycomb", 0.35))
				.tooltips(
						"item.quiufuits.ambar.tooltip.seed",
						"item.quiufuits.ambar.tooltip.hair",
						"item.quiufuits.ambar.tooltip.mob"
				)
				.build());

		for (FruitDefinition def : BY_ID.values()) {
			materialize(def);
		}
	}

	private static FruitDefinition register(FruitDefinition definition) {
		BY_ID.put(definition.id(), definition);
		return definition;
	}

	private static void materialize(FruitDefinition def) {
		String id = def.id();
		FruitCropBlock crop = new FruitCropBlock(() -> def, ModBlocks.fruitSettings());
		Registry.register(BuiltInRegistries.BLOCK, QuiuFruitsMod.id(id + "_fruit"), crop);

		FruitItem fruit = new FruitItem(() -> def, new Item.Properties());
		ModItems.register(id + "_fruit", fruit);

		FruitSeedItem seed = null;
		if (def.hasSeed()) {
			seed = new FruitSeedItem(() -> def, new Item.Properties());
			ModItems.register(id + "_seed", seed);
		}

		FruitHairItem hair = null;
		if (def.hasHair()) {
			hair = new FruitHairItem(() -> def, new Item.Properties());
			String hairId = def.hairNameKey() != null ? "pelo_" + def.hairNameKey() : id + "_hair";
			ModItems.register(hairId, hair);
		}

		// BlockItem for the crop (creative / debug)
		ModItems.register(id + "_fruit_block", new net.minecraft.world.item.BlockItem(crop, new Item.Properties()));

		def.bind(crop, fruit, seed, hair);
	}

	public static List<FruitDefinition> all() {
		return Collections.unmodifiableList(new ArrayList<>(BY_ID.values()));
	}

	public static FruitDefinition get(String id) {
		return BY_ID.get(id);
	}
}
