package com.example.voidapple;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VoidAppleMod.MOD_ID);

    // 1. Пустотное яблоко
    public static final RegistryObject<Item> VOID_APPLE = ITEMS.register("void_apple",
            () -> new Item(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .food(new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationMod(0.6f)
                            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1), 1.0f)
                            .alwaysEat()
                            .build())
            ));

    // 2. Посох Пустоты
    public static final RegistryObject<Item> VOID_STAFF = ITEMS.register("void_staff",
            () -> new VoidStaffItem(new Item.Properties()
                    .stacksTo(1) // Посох не стакается
                    .rarity(Rarity.EPIC)
            ));
}
