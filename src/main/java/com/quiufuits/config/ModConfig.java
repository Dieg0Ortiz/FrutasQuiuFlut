package com.quiufuits.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quiufuits.QuiuFruitsMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuración runtime (tiempos de maduración e IDs de integración).
 * Cambiar IDs del Plumosaurio aquí — sin recompilar — cuando el otro mod exista.
 */
public final class ModConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static ModConfig INSTANCE = new ModConfig();

	/** Probabilidad base (1/N) de avanzar etapa en cada random tick. */
	public int stageAdvanceChance = 5;
	/** Probabilidad (1/N) de pasar de Maduro a Podrido (más lento = más margen de cosecha). */
	public int ripeToRottenChance = 8;
	/** Nutrición base de las frutas. */
	public int fruitNutrition = 3;
	public float fruitSaturation = 0.4f;

	/**
	 * TODO(integración): cuando exista el mod Plumosaurio, ajusta estos IDs
	 * en config/quiufuits.json sin tocar código.
	 */
	public IntegrationIds plumosaurio = new IntegrationIds(
			"plumosaurio:plumosaurio",
			"plumosaurio:excremento",
			0.35
	);

	public static final class IntegrationIds {
		public String entityId;
		public String dropItemId;
		public double chance;

		public IntegrationIds() {
		}

		public IntegrationIds(String entityId, String dropItemId, double chance) {
			this.entityId = entityId;
			this.dropItemId = dropItemId;
			this.chance = chance;
		}
	}

	public static ModConfig get() {
		return INSTANCE;
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("quiufuits.json");
		try {
			if (Files.exists(path)) {
				try (Reader reader = Files.newBufferedReader(path)) {
					ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
					if (loaded != null) {
						INSTANCE = loaded;
					}
				}
			} else {
				Files.createDirectories(path.getParent());
				save(path);
			}
		} catch (Exception e) {
			QuiuFruitsMod.LOGGER.warn("No se pudo cargar quiufuits.json, usando valores por defecto: {}", e.toString());
			INSTANCE = new ModConfig();
		}
	}

	private static void save(Path path) throws IOException {
		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(INSTANCE, writer);
		}
	}

	private ModConfig() {
	}
}
