package com.example.voidapple;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VoidStaffItem extends Item {

    public VoidStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack staff = player.getItemInHand(hand);

        // Если зажат Shift (Sneak) — переключаем режим посоха
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                int nextMode = (getMode(staff) + 1) % 3;
                setMode(staff, nextMode);

                String modeName = switch (nextMode) {
                    case 0 -> "Телепортация";
                    case 1 -> "Пустотный взрыв";
                    case 2 -> "Защитная аура";
                    default -> "Неизвестно";
                };

                player.sendSystemMessage(Component.literal("§d[Посох Пустоты] §fРежим изменён на: §a" + modeName));
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.8f, 1.2f);
            }
            return InteractionResultHolder.sidedSuccess(staff, level.isClientSide());
        }

        // Если Shift НЕ зажат — активируем текущую способность
        if (!level.isClientSide()) {
            int currentMode = getMode(staff);

            switch (currentMode) {
                case 0 -> { // Телепортация (выстрел перлом)
                    ThrownEnderpearl pearl = new ThrownEnderpearl(level, player);
                    pearl.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.8F, 1.0F);
                    level.addFreshEntity(pearl);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENDER_PEARL_THROW, SoundSource.PLAYERS, 0.5F, 0.4F);
                    player.getCooldowns().addCooldown(this, 30); // Перезарядка 1.5 сек
                }
                case 1 -> { // Пустотный взрыв (череп визера)
                    WitherSkull skull = new WitherSkull(level, player, player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z);
                    skull.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                    level.addFreshEntity(skull);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.5F, 0.8F);
                    player.getCooldowns().addCooldown(this, 50); // Перезарядка 2.5 сек
                }
                case 2 -> { // Защита
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 1)); // Щит на 15 сек
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0)); // Сопротивление на 10 сек
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.7F, 1.0F);
                    player.getCooldowns().addCooldown(this, 200); // Перезарядка 10 сек
                }
            }
        }

        return InteractionResultHolder.sidedSuccess(staff, level.isClientSide());
    }

    // Чтение режима из NBT
    private int getMode(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt("StaffMode");
    }

    // Запись режима в NBT
    private void setMode(ItemStack stack, int mode) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("StaffMode", mode);
    }

    // Текст описания при наведении на предмет
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int mode = getMode(stack);
        String modeName = switch (mode) {
            case 0 -> "Телепортация";
            case 1 -> "Пустотный взрыв";
            case 2 -> "Защитная аура";
            default -> "Неизвестно";
        };

        tooltip.add(Component.literal("§7Текущий режим: §e" + modeName));
        tooltip.add(Component.literal("§8Нажмите §fShift + ПКМ§8, чтобы сменить режим"));
        tooltip.add(Component.literal("§8Нажмите §fПКМ§8, чтобы использовать способность"));
    }
}
