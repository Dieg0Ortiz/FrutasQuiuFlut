package com.quiufuits.fruits;

import com.quiufuits.blocks.fruit.FruitCropBlock;
import com.quiufuits.items.FruitHairItem;
import com.quiufuits.items.FruitItem;
import com.quiufuits.items.FruitSeedItem;
import net.minecraft.tags.TagKey;
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

		public FruitDefinition build() {
			if (effects == null || plantableOnLogs == null) {
				throw new IllegalStateException("FruitDefinition '" + id + "' requiere effects y plantableOn");
			}
			return new FruitDefinition(this);
		}
	}
}
