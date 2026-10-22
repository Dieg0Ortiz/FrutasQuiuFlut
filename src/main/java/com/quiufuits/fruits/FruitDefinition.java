package com.quiufuits.fruits;

import com.quiufuits.blocks.fruit.FruitCropBlock;
import com.quiufuits.blocks.fruit.FruitStage;
import com.quiufuits.items.FruitHairItem;
import com.quiufuits.items.FruitItem;
import com.quiufuits.items.FruitSeedItem;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Ficha de fruta: un solo lugar para bloques, ítems, efectos e interacciones opcionales.
 * Añadir una fruta nueva = crear una ficha y registrarla en {@link FruitDefinitions}.
 */
public final class FruitDefinition {
	private final String id;
	private final int itemColor;
	private final FruitEffectProfile effects;
	private final Supplier<TagKey<Block>> plantableOnLogs;
	private final boolean hasSeed;
	private final HairExtractMode hairExtractMode;
	@Nullable
	private final String hairItemId;
	@Nullable
	private final MobFeedSpec mobFeed;
	private final List<String> tooltipLines;
	private final List<String> seedTooltipLines;
	private final List<String> hairTooltipLines;
	private final int minDrops;
	private final int maxDrops;
	private final int ripeLightLevel;
	@Nullable
	private FruitCropBlock cropBlock;
	@Nullable
	private FruitItem fruitItem;
	@Nullable
	private FruitSeedItem seedItem;
	@Nullable
	private FruitHairItem hairItem;

	private FruitDefinition(Builder builder) {
		this.id = builder.id;
		this.itemColor = builder.itemColor;
		this.effects = builder.effects;
		this.plantableOnLogs = builder.plantableOnLogs;
		this.hasSeed = builder.hasSeed;
		this.hairExtractMode = builder.hairExtractMode;
		this.hairItemId = builder.hairItemId;
		this.mobFeed = builder.mobFeed;
		this.tooltipLines = List.copyOf(builder.tooltipLines);
		this.seedTooltipLines = List.copyOf(builder.seedTooltipLines);
		this.hairTooltipLines = List.copyOf(builder.hairTooltipLines);
		this.minDrops = builder.minDrops;
		this.maxDrops = builder.maxDrops;
		this.ripeLightLevel = builder.ripeLightLevel;
	}

	public String id() {
		return id;
	}

	public int itemColor() {
		return itemColor;
	}

	public FruitEffectProfile effects() {
		return effects;
	}

	public TagKey<Block> plantableOnLogs() {
		return plantableOnLogs.get();
	}

	public boolean hasSeed() {
		return hasSeed;
	}

	public boolean hasHair() {
		return hairExtractMode != HairExtractMode.NONE;
	}

	public HairExtractMode hairExtractMode() {
		return hairExtractMode;
	}

	@Nullable
	public String hairItemId() {
		return hairItemId;
	}

	public Optional<MobFeedSpec> mobFeed() {
		return Optional.ofNullable(mobFeed);
	}

	public List<String> tooltipLines() {
		return tooltipLines;
	}

	public List<String> seedTooltipLines() {
		return seedTooltipLines;
	}

	public List<String> hairTooltipLines() {
		return hairTooltipLines;
	}

	/** Cantidad de frutas al cosechar (entre min y max, ambos incluidos). */
	public int rollDropCount(RandomSource random) {
		return minDrops + random.nextInt(maxDrops - minDrops + 1);
	}

	/** Luz que emite el cultivo según su etapa (0 = no brilla). */
	public int lightLevel(FruitStage stage) {
		return switch (stage) {
			case RIPE -> ripeLightLevel;
			case UNDER_RIPE -> Math.max(0, ripeLightLevel - 2);
			case GROWING -> Math.max(0, ripeLightLevel - 4);
			case ROTTEN -> 0;
		};
	}

	public FruitCropBlock cropBlock() {
		return cropBlock;
	}

	public FruitItem fruitItem() {
		return fruitItem;
	}

	@Nullable
	public FruitSeedItem seedItem() {
		return seedItem;
	}

	@Nullable
	public FruitHairItem hairItem() {
		return hairItem;
	}

	void bind(FruitCropBlock crop, FruitItem fruit, @Nullable FruitSeedItem seed, @Nullable FruitHairItem hair) {
		this.cropBlock = crop;
		this.fruitItem = fruit;
		this.seedItem = seed;
		this.hairItem = hair;
	}

	public static Builder builder(String id) {
		return new Builder(id);
	}

	/**
	 * Interacción con mob: IDs configurables resueltos en runtime.
	 * Si {@code useModConfigPlumosaurio} es true, se leen entity/drop/chance de ModConfig.
	 */
	public record MobFeedSpec(
			boolean useModConfigPlumosaurio,
			@Nullable String entityId,
			@Nullable String dropItemId,
			double chance
	) {
		public static MobFeedSpec fromConfig() {
			return new MobFeedSpec(true, null, null, 0);
		}

		public static MobFeedSpec of(String entityId, String dropItemId, double chance) {
			return new MobFeedSpec(false, entityId, dropItemId, chance);
		}
	}

	public static final class Builder {
		private final String id;
		private int itemColor = 0xFFFFFF;
		private FruitEffectProfile effects;
		private Supplier<TagKey<Block>> plantableOnLogs;
		private boolean hasSeed = true;
		private HairExtractMode hairExtractMode = HairExtractMode.NONE;
		@Nullable
		private String hairItemId;
		@Nullable
		private MobFeedSpec mobFeed;
		private List<String> tooltipLines = Collections.emptyList();
		private List<String> seedTooltipLines = Collections.emptyList();
		private List<String> hairTooltipLines = Collections.emptyList();
		private int minDrops = 1;
		private int maxDrops = 1;
		private int ripeLightLevel = 0;

		private Builder(String id) {
			this.id = id;
		}

		public Builder color(int rgb) {
			this.itemColor = rgb;
			return this;
		}

		public Builder effects(FruitEffectProfile effects) {
			this.effects = effects;
			return this;
		}

		public Builder plantableOn(Supplier<TagKey<Block>> logs) {
			this.plantableOnLogs = logs;
			return this;
		}

		public Builder seed(boolean hasSeed) {
			this.hasSeed = hasSeed;
			return this;
		}

		/** Pelos con Tijeras + fruta en mano secundaria (como Huevo Eburia). */
		public Builder hairWithShears(String hairItemId) {
			this.hairExtractMode = HairExtractMode.SHEARS_OFFHAND;
			this.hairItemId = hairItemId;
			return this;
		}

		public Builder noHair() {
			this.hairExtractMode = HairExtractMode.NONE;
			this.hairItemId = null;
			return this;
		}

		public Builder mobFeed(@Nullable MobFeedSpec mobFeed) {
			this.mobFeed = mobFeed;
			return this;
		}

		public Builder tooltips(String... lines) {
			this.tooltipLines = List.of(lines);
			return this;
		}

		public Builder seedTooltips(String... lines) {
			this.seedTooltipLines = List.of(lines);
			return this;
		}

		public Builder hairTooltips(String... lines) {
			this.hairTooltipLines = List.of(lines);
			return this;
		}

		/** Frutas que suelta al cosecharla (poco madura o madura). */
		public Builder dropCount(int min, int max) {
			if (min < 1 || max < min) {
				throw new IllegalArgumentException("dropCount inválido: " + min + ".." + max);
			}
			this.minDrops = min;
			this.maxDrops = max;
			return this;
		}

		/** Brillo leve en la oscuridad: luz al estar madura (menos en etapas anteriores). */
		public Builder glow(int ripeLightLevel) {
			this.ripeLightLevel = ripeLightLevel;
			return this;
		}

		public FruitDefinition build() {
			if (effects == null || plantableOnLogs == null) {
				throw new IllegalStateException("FruitDefinition '" + id + "' requiere effects y plantableOn");
			}
			return new FruitDefinition(this);
		}
	}
}
