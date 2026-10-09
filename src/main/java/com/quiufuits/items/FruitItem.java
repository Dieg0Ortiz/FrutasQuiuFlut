package com.quiufuits.items;

import com.quiufuits.blocks.fruit.FruitStage;
import com.quiufuits.config.ModConfig;
import com.quiufuits.fruits.FruitDefinition;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class FruitItem extends Item {
	public static final String STAGE_TAG = "FruitStage";

	private final Supplier<FruitDefinition> definition;

	public FruitItem(Supplier<FruitDefinition> definition, Properties properties) {
		super(properties.food(new FoodProperties.Builder()
				.nutrition(ModConfig.get().fruitNutrition)
				.saturationMod(ModConfig.get().fruitSaturation)
				.alwaysEat()
				.build()));
		this.definition = definition;
	}

	public FruitDefinition definition() {
		return definition.get();
	}

	public static ItemStack create(FruitItem item, FruitStage stage) {
		ItemStack stack = new ItemStack(item);
		setStage(stack, stage);
		return stack;
	}

	public static void setStage(ItemStack stack, FruitStage stage) {
		stack.getOrCreateTag().putString(STAGE_TAG, stage.getSerializedName());
	}

	public static FruitStage getStage(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains(STAGE_TAG)) {
			return FruitStage.RIPE;
		}
		String name = tag.getString(STAGE_TAG);
		for (FruitStage stage : FruitStage.values()) {
			if (stage.getSerializedName().equals(name)) {
				return stage;
			}
		}
		return FruitStage.RIPE;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		FruitStage stage = getStage(stack);
		ItemStack result = super.finishUsingItem(stack, level, entity);
		// Podrida: se puede comer y sacar semilla, pero no aplica ningún efecto
		if (!level.isClientSide && stage != FruitStage.ROTTEN && stage != FruitStage.GROWING) {
			MobEffectInstance effect = switch (stage) {
				case UNDER_RIPE -> definition().effects().underRipeInstance();
				case RIPE -> definition().effects().ripeInstance();
				default -> null;
			};
			if (effect != null) {
				entity.addEffect(new MobEffectInstance(effect));
			}
		}
		return result;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		FruitStage stage = getStage(stack);
		if (stage != FruitStage.RIPE) {
			tooltip.add(Component.translatable("item.quiufuits.fruit.stage." + stage.getTranslationKey())
					.withStyle(stageStyle(stage)));
		}

		for (String lineKey : definition().tooltipLines()) {
			ChatFormatting style = lineKey.contains("warning") ? ChatFormatting.GOLD : null;
			Component line = Component.translatable(lineKey);
			if (style != null) {
				tooltip.add(line.copy().withStyle(style));
			} else {
				tooltip.add(line);
			}
		}
	}

	private static ChatFormatting stageStyle(FruitStage stage) {
		return switch (stage) {
			case GROWING -> ChatFormatting.GRAY;
			case UNDER_RIPE -> ChatFormatting.YELLOW;
			case RIPE -> ChatFormatting.GREEN;
			case ROTTEN -> ChatFormatting.DARK_RED;
		};
	}

	@Override
	public Component getName(ItemStack stack) {
		FruitStage stage = getStage(stack);
		if (stage == FruitStage.RIPE) {
			return super.getName(stack);
		}
		return Component.translatable(getDescriptionId())
				.append(Component.literal(" ("))
				.append(Component.translatable("item.quiufuits.fruit.stage." + stage.getTranslationKey()))
				.append(Component.literal(")"));
	}
}
