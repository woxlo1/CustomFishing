package Ora000.customfishing.model;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class CustomFish {

    // ===============================
    // 基本情報
    // ===============================
    public String name;
    public String rarity;
    public String lore;

    // ✅ 見た目
    public Material material;
    public int customModelData;

    // ===============================
    // 抽選・サイズ
    // ===============================
    public double probability;
    public int minSize, maxSize;

    // ===============================
    // 切り身
    // ===============================
    public boolean filletingEnabled = false;
    public String filletName = "";
    public int filletMinAmount = 1;
    public int filletMaxAmount = 3;
    public int filletHunger = 4;
    public double filletSaturation = 0.5;
    public int filletCustomModelData = 0;

    // ===============================
    // 鮮度
    // ===============================
    public int minFreshness = 50;
    public int maxFreshness = 100;

    // ===============================
    // 効果
    // ===============================
    public List<PotionEffect> potionEffects = new ArrayList<>();

    // ===============================
    // constructor
    // ===============================
    public CustomFish(
            String name,
            double probability,
            String rarity,
            String lore,
            int minSize,
            int maxSize,
            int customModelData
    ) {
        this.name = name;
        this.probability = probability;
        this.rarity = rarity;
        this.lore = lore;
        this.material = material;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.customModelData = customModelData;
    }
}
