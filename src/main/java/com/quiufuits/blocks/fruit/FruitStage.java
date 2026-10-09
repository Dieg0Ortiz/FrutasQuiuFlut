package com.quiufuits.blocks.fruit;

import net.minecraft.util.StringRepresentable;

public enum FruitStage implements StringRepresentable {
	GROWING(0, "growing", "creciendo"),
	UNDER_RIPE(1, "under_ripe", "poco_maduro"),
	RIPE(2, "ripe", "maduro"),
	ROTTEN(3, "rotten", "podrido");

	private final int index;
	private final String name;
	private final String translationKey;

	FruitStage(int index, String name, String translationKey) {
		this.index = index;
		this.name = name;
		this.translationKey = translationKey;
	}

	public int getIndex() {
		return index;
	}

	public String getTranslationKey() {
		return translationKey;
	}

	public boolean canHarvest() {
		return this != GROWING;
	}

	public FruitStage next() {
		return switch (this) {
			case GROWING -> UNDER_RIPE;
			case UNDER_RIPE -> RIPE;
			case RIPE -> ROTTEN;
			case ROTTEN -> ROTTEN;
		};
	}

	public static FruitStage byIndex(int index) {
		for (FruitStage stage : values()) {
			if (stage.index == index) {
				return stage;
			}
		}
		return GROWING;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
