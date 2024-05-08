package com.kltyton.name.push_forward.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "push_forward.json");
    public float damageRateOfMultiplication = 5;
    public double knockbackRateOfMultiplication = 1.0;
    public double velocityThreshold = 5.0;
    public float maxDamage = 20;
    public float minDamage = 0;
    public double maxKnockback = 5.0;
    public double minKnockback = 0.0;
    public double hurtBackRateOfMultiplication = 1.0;
    public boolean hurtBack = false;
    public boolean blackListEnabled = false;
    public boolean whiteListEnabled = false;
    public List<String> blackList = new ArrayList<>();
    public List<String> whiteList = new ArrayList<>();

    public static ModConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = new String(Files.readAllBytes(CONFIG_PATH));
                return GSON.fromJson(json, ModConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ModConfig config = new ModConfig();
        config.save();
        return config;
    }

    public void save() {
        String json = GSON.toJson(this);
        try {
            Files.write(CONFIG_PATH, json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}