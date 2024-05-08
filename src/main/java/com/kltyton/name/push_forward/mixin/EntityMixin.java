package com.kltyton.name.push_forward.mixin;

import com.kltyton.name.push_forward.PushForward;
import com.kltyton.name.push_forward.config.ModConfig;
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
    private static final ModConfig CONFIG = ModConfig.load();

    @Unique
    private static final float DAMAGE_RATE_OF_MULTIPLICATION = CONFIG.damageRateOfMultiplication;
    @Unique
    private static final double KNOCKBACK_RATE_OF_MULTIPLICATION = CONFIG.knockbackRateOfMultiplication;
    @Unique
    private static final double VELOCITY_THRESHOLD = CONFIG.velocityThreshold;
    @Unique
    private static final float MAX_DAMAGE = CONFIG.maxDamage;
    @Unique
    private static final float MIN_DAMAGE = CONFIG.minDamage;
    @Unique
    private static final double MAX_KNOCKBACK = CONFIG.maxKnockback;
    @Unique
    private static final double MIN_KNOCKBACK = CONFIG.minKnockback;
    @Unique
    private static final double HURT_BACK_RATE_OF_MULTIPLICATION = CONFIG.hurtBackRateOfMultiplication;
    @Unique
    private static final boolean HURT_BACK = CONFIG.hurtBack;
    @Unique
    private static final boolean BLACKLIST_ENABLED = CONFIG.blackListEnabled;
    @Unique
    private static final boolean WHITELIST_ENABLED = CONFIG.whiteListEnabled;
    @Unique
    private static final List<String> BLACKLIST = CONFIG.blackList;
    @Unique
    private static final List<String> WHITELIST = CONFIG.whiteList;

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
        double thisSpeed = thisVelocity.length() * 20;
        double otherSpeed = otherVelocity.length() * 20;
        // thisEntity 的速度大于 entity 的速度
        if (thisSpeed > otherSpeed && thisSpeed >= VELOCITY_THRESHOLD) {
            float damage = (float) ((thisSpeed - otherSpeed) * DAMAGE_RATE_OF_MULTIPLICATION);
            damage = Math.max(damage, MIN_DAMAGE);
            damage = Math.min(damage, MAX_DAMAGE);
            double knockback = (thisSpeed - otherSpeed) * KNOCKBACK_RATE_OF_MULTIPLICATION;
            knockback = Math.max(knockback, MIN_KNOCKBACK);
            knockback = Math.min(knockback, MAX_KNOCKBACK);
            entity.damage(PushForward.of(world, PushForward.COLLIDE), damage);
            Vec3d knockbackDirection = entity.getPos().subtract(thisEntity.getPos()).normalize();
            entity.addVelocity(knockbackDirection.x * knockback, knockbackDirection.y * knockback, knockbackDirection.z * knockback);
            thisEntity.getDataTracker().set(IS_PUSHING, true);
            if (HURT_BACK) {
                thisEntity.damage(PushForward.of(world, PushForward.COLLIDE), (float) (damage * HURT_BACK_RATE_OF_MULTIPLICATION));
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
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
