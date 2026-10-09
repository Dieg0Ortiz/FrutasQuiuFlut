package com.quiufuits;

import com.quiufuits.config.ModConfig;
import com.quiufuits.fruits.FruitDefinitions;
import com.quiufuits.integration.MobFeedIntegration;
import com.quiufuits.registry.ModBlocks;
import com.quiufuits.registry.ModItemGroups;
import com.quiufuits.registry.ModItems;
import com.quiufuits.registry.ModRegistries;
import com.quiufuits.worldgen.ModWorldGen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuiuFruitsMod implements ModInitializer {
	public static final String MOD_ID = "quiufuits";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfig.load();
		ModBlocks.register();
		ModItems.register();
		FruitDefinitions.bootstrap();
		ModItemGroups.register();
		ModRegistries.registerCallbacks();
		MobFeedIntegration.register();
		ModWorldGen.register();
		LOGGER.info("Quiu Fruits listo — {} frutas registradas.", FruitDefinitions.all().size());
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
