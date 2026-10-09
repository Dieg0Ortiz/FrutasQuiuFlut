package com.quiufuits.integration;

import com.quiufuits.QuiuFruitsMod;
import com.quiufuits.config.ModConfig;
import com.quiufuits.fruits.FruitDefinition;
import com.quiufuits.fruits.FruitDefinitions;
import com.quiufuits.items.FruitItem;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Alimentar mobs con frutas. Resuelve entidad e ítem por ID en runtime.
 *
 * <p>TODO(integración Plumosaurio): el mod aún no existe. IDs placeholder en
 * {@code config/quiufuits.json} ({@code plumosaurio.entityId} / {@code dropItemId}).
 * Cuando el otro mod cargue, la interacción funciona sin recompilar.
 * Si la entidad o el ítem no existen → warning en log, NUNCA crash.
 */
public final class MobFeedIntegration {
	private static final Set<String> WARNED_MISSING = new HashSet<>();

	private MobFeedIntegration() {
	}

	public static void register() {
		UseEntityCallback.EVENT.register(MobFeedIntegration::onUseEntity);
		QuiuFruitsMod.LOGGER.info(
				"Integración Plumosaurio preparada (placeholder). entity={}, drop={}. "
						+ "TODO: conectar cuando exista el mod.",
				ModConfig.get().plumosaurio.entityId,
				ModConfig.get().plumosaurio.dropItemId
		);
	}

	private static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand,
			Entity entity, @Nullable EntityHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		if (!(stack.getItem() instanceof FruitItem fruitItem)) {
			return InteractionResult.PASS;
		}

		FruitDefinition def = fruitItem.definition();
		Optional<FruitDefinition.MobFeedSpec> feedOpt = def.mobFeed();
		if (feedOpt.isEmpty()) {
			return InteractionResult.PASS;
		}

		ResolvedFeed feed = resolve(feedOpt.get(), def.id());
		if (feed == null) {
			return InteractionResult.PASS;
		}

		if (!entity.getType().equals(feed.entityType)) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide) {
			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
			if (level.random.nextDouble() < feed.chance) {
				ItemEntity drop = new ItemEntity(level, entity.getX(), entity.getY() + 0.5, entity.getZ(),
						new ItemStack(feed.dropItem));
				level.addFreshEntity(drop);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Nullable
	private static ResolvedFeed resolve(FruitDefinition.MobFeedSpec spec, String fruitId) {
		String entityId;
		String dropId;
		double chance;

		if (spec.useModConfigPlumosaurio()) {
			ModConfig.IntegrationIds cfg = ModConfig.get().plumosaurio;
			entityId = cfg.entityId;
			dropId = cfg.dropItemId;
			chance = cfg.chance;
		} else {
			entityId = spec.entityId();
			dropId = spec.dropItemId();
			chance = spec.chance();
		}

		Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(new ResourceLocation(entityId));
		Optional<Item> dropItem = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(dropId));

		if (entityType.isEmpty()) {
			warnOnce("entity:" + entityId, fruitId,
					"Entidad '{}' no encontrada para fruta '{}'. Interacción desactivada hasta que el mod exista.",
					entityId, fruitId);
			return null;
		}
		if (dropItem.isEmpty()) {
			warnOnce("item:" + dropId, fruitId,
					"Ítem '{}' no encontrado para fruta '{}'. Interacción desactivada hasta que el mod exista.",
					dropId, fruitId);
			return null;
		}

		return new ResolvedFeed(entityType.get(), dropItem.get(), chance);
	}

	private static void warnOnce(String key, String fruitId, String message, Object... args) {
		if (WARNED_MISSING.add(key)) {
			QuiuFruitsMod.LOGGER.warn(message, args);
		}
	}

	private record ResolvedFeed(EntityType<?> entityType, Item dropItem, double chance) {
	}
}
