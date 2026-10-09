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
 * Frutas oficiales del mod (según capturas de referencia).
 *
 * <p><b>Fruta Solaria — Fire Resistance</b>: calor solar → protección térmica.
 * <p><b>Huevo Eburia — Health Boost</b>: +4 salud máxima (como en la ficha de referencia).
 * <p><b>Arándano Nocturino — Night Vision</b>: tema nocturno.
 * <p><b>Baya Uvina — Regeneration</b>: baya dulce restauradora.
 * <p>Etapa podrida: sin efecto al comer; la semilla sí se puede extraer con hacha.
 */
public final class FruitDefinitions {
	private static final Map<String, FruitDefinition> BY_ID = new LinkedHashMap<>();

	public static FruitDefinition SOLARIA;
	public static FruitDefinition EBURIA;
	public static FruitDefinition NOCTURINO;
	public static FruitDefinition UVINA;

	private FruitDefinitions() {
	}

	public static void bootstrap() {
		SOLARIA = register(FruitDefinition.builder("solaria")
				.color(0xFFC94A)
				.effects(FruitEffectProfile.beneficial(
						MobEffects.FIRE_RESISTANCE, 8 * 20, 0,
						MobEffects.FIRE_RESISTANCE, 45 * 20, 0
				))
				.plantableOn(() -> ModTags.FRUIT_PLANTABLE_LOGS)
				.seed(true)
				.noHair()
				.mobFeed(FruitDefinition.MobFeedSpec.fromConfig())
				.tooltips(
						"item.quiufuits.fruit.find_centro_quiu",
						"item.quiufuits.fruit.warning",
						"item.quiufuits.solaria.tooltip.mob",
						"item.quiufuits.solaria.tooltip.seed"
				)
				.seedTooltips(
						"item.quiufuits.seed.plant_abedul_quiu",
						"item.quiufuits.fruit.warning",
						"item.quiufuits.solaria.tooltip.seed"
				)
				.build());

		// Huevo Eburia: Salud mejorada 1:00 / Salud máxima +4 (HEALTH_BOOST amp 0)
		EBURIA = register(FruitDefinition.builder("eburia")
				.color(0xD4B896)
				.effects(FruitEffectProfile.beneficial(
						MobEffects.HEALTH_BOOST, 20 * 20, 0,
						MobEffects.HEALTH_BOOST, 60 * 20, 0
				))
				.plantableOn(() -> ModTags.FRUIT_PLANTABLE_LOGS)
				.seed(true)
				.hairWithShears("pelos_eburia")
				.tooltips(
						"item.quiufuits.fruit.find_centro_quiu",
						"item.quiufuits.fruit.warning",
						"item.quiufuits.eburia.tooltip.hair",
						"item.quiufuits.eburia.tooltip.seed"
				)
				.seedTooltips(
						"item.quiufuits.seed.plant_abedul_quiu",
						"item.quiufuits.fruit.warning",
						"item.quiufuits.eburia.tooltip.seed"
				)
				.hairTooltips("item.quiufuits.eburia.tooltip.hair_item")
				.build());

		NOCTURINO = register(FruitDefinition.builder("nocturino")
				.color(0x3A5FBF)
				.effects(FruitEffectProfile.beneficial(
						MobEffects.NIGHT_VISION, 15 * 20, 0,
						MobEffects.NIGHT_VISION, 90 * 20, 0
				))
				.plantableOn(() -> ModTags.FRUIT_PLANTABLE_LOGS)
				.seed(true)
				.noHair()
				.tooltips(
						"item.quiufuits.fruit.find_centro_quiu",
						"item.quiufuits.fruit.warning",
						"item.quiufuits.nocturino.tooltip.seed"
				)
				.seedTooltips(
						"item.quiufuits.seed.plant_abedul_quiu",
						"item.quiufuits.fruit.warning",
						"item.quiufuits.nocturino.tooltip.seed"
				)
				.build());

		UVINA = register(FruitDefinition.builder("uvina")
				.color(0xE05AA8)
				.effects(FruitEffectProfile.beneficial(
						MobEffects.REGENERATION, 5 * 20, 0,
						MobEffects.REGENERATION, 12 * 20, 0
				))
				.plantableOn(() -> ModTags.FRUIT_PLANTABLE_LOGS)
				.seed(true)
				.noHair()
				.tooltips(
						"item.quiufuits.fruit.find_centro_quiu",
						"item.quiufuits.fruit.warning",
						"item.quiufuits.uvina.tooltip.seed"
				)
				.seedTooltips(
						"item.quiufuits.seed.plant_abedul_quiu",
						"item.quiufuits.fruit.warning",
						"item.quiufuits.uvina.tooltip.seed"
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
			String hairId = def.hairItemId() != null ? def.hairItemId() : id + "_hair";
			ModItems.register(hairId, hair);
		}

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
