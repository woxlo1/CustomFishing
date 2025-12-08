package Ora000.customfishing.model;

import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class CustomFish {

    public String name;
    public String rarity;
    public String lore;
    public String filletName = "";

    public double probability;
    public double filletSaturation = 0.5;

    public int minSize, maxSize, customModelData;
    public int filletMinAmount = 1, filletMaxAmount = 3;
    public int filletHunger = 4;
    public int filletCustomModelData = 0;

    public int minFreshness = 50, maxFreshness = 100;

    public boolean filletingEnabled = false;

    public List<PotionEffect> potionEffects = new ArrayList<>();

    public CustomFish(String n, double pr, String r, String l,
                      int mins, int maxs, int cmd) {
        this.name = n;
        this.probability = pr;
        this.rarity = r;
        this.lore = l;
        this.minSize = mins;
        this.maxSize = maxs;
        this.customModelData = cmd;
    }
}
