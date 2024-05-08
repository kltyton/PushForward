package com.kltyton.name.push_forward.mixin;

import com.kltyton.name.push_forward.PushForward;
import com.kltyton.name.push_forward.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    private static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Unique
    private static final float DAMAGE_RATE_OF_MULTIPLICATION = getConfig().getDamageRateOfMultiplication();
    @Unique
    private static final double KNOCKBACK_RATE_OF_MULTIPLICATION = getConfig().getKnockbackRateOfMultiplication();
    @Unique
    private static final double VELOCITY_THRESHOLD = getConfig().getVelocityThreshold();
    @Unique
    private static final float MAX_DAMAGE = getConfig().getMaxDamage();
    @Unique
    private static final float MIN_DAMAGE = getConfig().getMinDamage();
    @Unique
    private static final double MAX_KNOCKBACK = getConfig().getMaxKnockback();
    @Unique
    private static final double MIN_KNOCKBACK = getConfig().getMinKnockback();
    @Unique
    private static final double HURT_BACK_RATE_OF_MULTIPLICATION = getConfig().getHurtBackRateOfMultiplication();
    @Unique
    private static final boolean HURT_BACK = getConfig().isHurtBack();
    @Unique
    private static final boolean BLACKLIST_ENABLED = getConfig().isBlackListEnabled();
    @Unique
    private static final boolean WHITELIST_ENABLED = getConfig().isWhiteListEnabled();
    @Unique
    private static final List<String> BLACKLIST = getConfig().getBlackList();
    @Unique
    private static final List<String> WHITELIST = getConfig().getWhiteList();

    @Shadow private World world;

    // 添加一个自定义的数据值
    @Unique
    private static final TrackedData<Boolean> IS_PUSHING = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
        thisEntity.getDataTracker().startTracking(IS_PUSHING, false);
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void onPushAwayFrom(@NotNull Entity entity, CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;

        // 检查黑名单和白名单
        if ((BLACKLIST_ENABLED && isInBlacklist(entity)) || (WHITELIST_ENABLED && !isInWhitelist(entity))) {
            ci.cancel();
            return;
        }

        // 计算两个实体的速度
        Vec3d thisVelocity = thisEntity.getVelocity();
        Vec3d otherVelocity = entity.getVelocity();
        double thisSpeed = thisVelocity.length();  // 方块/刻
        double otherSpeed = otherVelocity.length();  // 方块/刻
        // 如果 thisEntity 的速度大于 entity 的速度
        if (thisSpeed > otherSpeed && thisSpeed >= VELOCITY_THRESHOLD) {
            // 计算撞击伤害，这里假设速度的平方作为伤害值，你可以根据需要调整这个公式
            float damage = (float) ((thisSpeed - otherSpeed) * DAMAGE_RATE_OF_MULTIPLICATION);
            damage = Math.max(damage, MIN_DAMAGE); // 使用 Math.max 方法限制伤害值不小于最小伤害值
            damage = Math.min(damage, MAX_DAMAGE); // 使用 Math.min 方法限制伤害值不超过最大伤害值
            double knockback = (thisSpeed - otherSpeed) * KNOCKBACK_RATE_OF_MULTIPLICATION;
            knockback = Math.max(knockback, MIN_KNOCKBACK); // 使用 Math.max 方法限制击退值不小于最小击退值
            knockback = Math.min(knockback, MAX_KNOCKBACK); // 使用 Math.min 方法限制击退值不超过最大击退值
            // 创建一个自定义的伤害源
            entity.damage(PushForward.of(world, PushForward.COLLIDE), damage);
            // 添加击退效果
            Vec3d knockbackDirection = entity.getPos().subtract(thisEntity.getPos()).normalize();
            entity.addVelocity(knockbackDirection.x * knockback, knockbackDirection.y * knockback, knockbackDirection.z * knockback);
            // 标记 thisEntity 为发起撞击的实体
            thisEntity.getDataTracker().set(IS_PUSHING, true);
            if (HURT_BACK) {
                thisEntity.damage(PushForward.of(world, PushForward.COLLIDE), (float) (damage * HURT_BACK_RATE_OF_MULTIPLICATION));
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
        // 在每个刻的开始时，重置 IS_PUSHING 的值
        if (thisEntity.getDataTracker().get(IS_PUSHING)) {
            thisEntity.getDataTracker().set(IS_PUSHING, false);
        }
    }

    @Unique
    private boolean isInBlacklist(Entity entity) {
        return BLACKLIST.contains(entity.getType().getTranslationKey());
    }

    @Unique
    private boolean isInWhitelist(Entity entity) {
        return WHITELIST.isEmpty() || WHITELIST.contains(entity.getType().getTranslationKey());
    }
}
