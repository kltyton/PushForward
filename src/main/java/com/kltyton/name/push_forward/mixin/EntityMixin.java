package com.kltyton.name.push_forward.mixin;

import com.kltyton.name.push_forward.PushForward;
import com.kltyton.name.push_forward.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
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
    private static final int KNOCKBACK_TICK_TIME = CONFIG.knockbackTickTime;
    @Unique
    private static final List<String> BLACKLIST = CONFIG.blackList;
    @Unique
    private static final List<String> WHITELIST = CONFIG.whiteList;
    @Unique
    private int delayTicks = KNOCKBACK_TICK_TIME;
    @Unique
    private static final boolean SENSITIVE_MODE_ENABLED = CONFIG.sensitiveModeEnabled;
    @Unique
    private static final double COLLISION_BOX_EXPANSION = CONFIG.collisionBoxExpansion;
    @Shadow private World world;
    @Unique
    private Entity pushedEntity = null;
    // 添加一个自定义的数据值
    @Unique
    private static final TrackedData<Boolean> IS_PUSHING = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
        thisEntity.getDataTracker().startTracking(IS_PUSHING, false);
    }

    @Unique
    private Vec3d nextVelocity = null;

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void onPushAwayFrom(@NotNull Entity entity, CallbackInfo ci) {
        if (!SENSITIVE_MODE_ENABLED) {
            Entity thisEntity = (Entity) (Object) this;
            // 检查黑名单和白名单
            if ((BLACKLIST_ENABLED && isInBlacklist(entity)) || (WHITELIST_ENABLED && isInWhitelist(entity)) || entity instanceof PlayerEntity) {
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
                // 计算相对速度
                double relativeSpeed = thisSpeed - otherSpeed;
                float damage = (float) (relativeSpeed * DAMAGE_RATE_OF_MULTIPLICATION);
                damage = Math.max(damage, MIN_DAMAGE);
                damage = Math.min(damage, MAX_DAMAGE);
                double knockback = relativeSpeed * KNOCKBACK_RATE_OF_MULTIPLICATION;
                knockback = Math.max(knockback, MIN_KNOCKBACK);
                knockback = Math.min(knockback, MAX_KNOCKBACK);
                entity.damage(PushForward.of(world, PushForward.COLLIDE), damage);
                Vec3d knockbackDirection = entity.getPos().subtract(thisEntity.getPos()).normalize();
                nextVelocity = new Vec3d(knockbackDirection.x * knockback, knockbackDirection.y * knockback, knockbackDirection.z * knockback);
                thisEntity.getDataTracker().set(IS_PUSHING, true);
                if (HURT_BACK && !thisEntity.getWorld().isSpaceEmpty(thisEntity.getBoundingBox().expand(COLLISION_BOX_EXPANSION))) {
                    thisEntity.damage(PushForward.of(world, PushForward.COLLIDE), (float) (thisSpeed * DAMAGE_RATE_OF_MULTIPLICATION));
                    thisEntity.addVelocity(knockbackDirection.x * knockback * HURT_BACK_RATE_OF_MULTIPLICATION, knockbackDirection.y * knockback * HURT_BACK_RATE_OF_MULTIPLICATION, knockbackDirection.z * knockback * HURT_BACK_RATE_OF_MULTIPLICATION);
                }
                this.pushedEntity = entity;
                ci.cancel();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
        if (thisEntity.getDataTracker().get(IS_PUSHING)) {
            thisEntity.getDataTracker().set(IS_PUSHING, false);
            if (delayTicks > 0) {
                delayTicks--;
                if (delayTicks > 0)
                    return;
            }
            if (this.pushedEntity != null && nextVelocity != null) {
                this.pushedEntity.addVelocity(nextVelocity.x, nextVelocity.y, nextVelocity.z);
                nextVelocity = null;
            }
        }

        // 如果敏感模式开启，执行敏感模式的逻辑
        if (SENSITIVE_MODE_ENABLED) {
            List<Entity> collidingEntities = thisEntity.getWorld().getOtherEntities(thisEntity, thisEntity.getBoundingBox().expand(COLLISION_BOX_EXPANSION));
            for (Entity entity : collidingEntities) {
                // 检查黑名单和白名单
                if ((BLACKLIST_ENABLED && isInBlacklist(entity)) || (WHITELIST_ENABLED && isInWhitelist(entity)) || entity instanceof PlayerEntity) {
                    continue;
                }
                // 计算两个实体的速度
                Vec3d thisVelocity = thisEntity.getVelocity();
                Vec3d otherVelocity = entity.getVelocity();
                double thisSpeed = thisVelocity.length() * 20;
                double otherSpeed = otherVelocity.length() * 20;
                if (thisSpeed > otherSpeed && thisSpeed >= VELOCITY_THRESHOLD) {
                    // 计算相对速度
                    double relativeSpeed = thisSpeed - otherSpeed;
                    float damage = (float) (relativeSpeed * DAMAGE_RATE_OF_MULTIPLICATION);
                    damage = Math.max(damage, MIN_DAMAGE);
                    damage = Math.min(damage, MAX_DAMAGE);
                    double knockback = relativeSpeed * KNOCKBACK_RATE_OF_MULTIPLICATION;
                    knockback = Math.max(knockback, MIN_KNOCKBACK);
                    knockback = Math.min(knockback, MAX_KNOCKBACK);
                    entity.damage(PushForward.of(world, PushForward.COLLIDE), damage);
                    Vec3d knockbackDirection = entity.getPos().subtract(thisEntity.getPos()).normalize();
                    nextVelocity = new Vec3d(knockbackDirection.x * knockback, knockbackDirection.y * knockback, knockbackDirection.z * knockback);
                    thisEntity.getDataTracker().set(IS_PUSHING, true);
                    if (HURT_BACK && !thisEntity.getWorld().isSpaceEmpty(thisEntity.getBoundingBox().expand(COLLISION_BOX_EXPANSION))) {
                        thisEntity.damage(PushForward.of(world, PushForward.COLLIDE), (float) (thisSpeed * DAMAGE_RATE_OF_MULTIPLICATION));
                        thisEntity.addVelocity(knockbackDirection.x * knockback * HURT_BACK_RATE_OF_MULTIPLICATION, knockbackDirection.y * knockback * HURT_BACK_RATE_OF_MULTIPLICATION, knockbackDirection.z * knockback * HURT_BACK_RATE_OF_MULTIPLICATION);
                    }
                    this.pushedEntity = entity;
                }
            }
        }
    }

    @Unique
    private boolean isInBlacklist(Entity entity) {
        return BLACKLIST.contains(entity.getType().getTranslationKey());
    }

    @Unique
    private boolean isInWhitelist(Entity entity) {
        return !WHITELIST.isEmpty() && !WHITELIST.contains(entity.getType().getTranslationKey());
    }
}