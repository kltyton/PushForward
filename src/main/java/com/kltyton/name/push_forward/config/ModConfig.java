package com.kltyton.name.push_forward.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@Config(name = "push_forward")
public class ModConfig implements ConfigData {
    public float damageRateOfMultiplication = 5;
    public double knockbackRateOfMultiplication = 1.0;
    public double velocityThreshold = 5.0;
    public float maxDamage = 20;
    public float minDamage = 1;
    public double maxKnockback = 5.0;
    public double minKnockback = 0.1;
    public double hurtBackRateOfMultiplication = 1.0;
    public boolean hurtBack = false;
    public boolean blackListEnabled = false;
    public boolean whiteListEnabled = false;
    public List<String> blackList = new ArrayList<>();
    public List<String> whiteList = new ArrayList<>();

    public float getDamageRateOfMultiplication() {
        return damageRateOfMultiplication;
    }

    public double getKnockbackRateOfMultiplication() {
        return knockbackRateOfMultiplication;
    }

    public double getVelocityThreshold() {
        return velocityThreshold;
    }

    public float getMaxDamage() {
        return maxDamage;
    }

    public float getMinDamage() {
        return minDamage;
    }

    public double getMaxKnockback() {
        return maxKnockback;
    }

    public double getMinKnockback() {
        return minKnockback;
    }

    public double getHurtBackRateOfMultiplication() {
        return hurtBackRateOfMultiplication;
    }

    public boolean isHurtBack() {
        return hurtBack;
    }

    public boolean isBlackListEnabled() {
        return blackListEnabled;
    }

    public boolean isWhiteListEnabled() {
        return whiteListEnabled;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }
}
