package com.kltyton.name.push_forward;

import com.kltyton.name.push_forward.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushForward implements ModInitializer {
    public static final String MOD_ID = "push_forward";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RegistryKey<DamageType> COLLIDE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(MOD_ID,"collide"));
    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing PushForward");
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
    }
}