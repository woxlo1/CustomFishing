package Ora000.customfishing.data;

import Ora000.customfishing.model.CustomFish;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FishDataManager {

    private final JavaPlugin plugin;
    private final File fishDataFile;
    private FileConfiguration config;

    public final List<CustomFish> fishes = new ArrayList<>();

    public FishDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.fishDataFile = new File(plugin.getDataFolder(), "fish_data.yml");

        if (!fishDataFile.exists()) {
            try {
                fishDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("fish_data.yml を作成できません: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(fishDataFile);
    }

    // ==================================================
    // 読み込み
    // ==================================================
    public void loadFishData() {
        fishes.clear();
        config = YamlConfiguration.loadConfiguration(fishDataFile);

        if (!config.contains("fishes")) return;

        for (String key : config.getConfigurationSection("fishes").getKeys(false)) {
            String p = "fishes." + key;

            CustomFish f = new CustomFish(
                    config.getString(p + ".name"),
                    config.getDouble(p + ".probability"),
                    config.getString(p + ".rarity"),
                    config.getString(p + ".lore"),
                    config.getInt(p + ".minSize"),
                    config.getInt(p + ".maxSize"),
                    config.getInt(p + ".customModelData")
            );

            if (config.contains(p + ".filleting")) {
                String fp = p + ".filleting";
                f.filletingEnabled = config.getBoolean(fp + ".enabled");
                f.filletName = config.getString(fp + ".name", "");
                f.filletMinAmount = config.getInt(fp + ".minAmount");
                f.filletMaxAmount = config.getInt(fp + ".maxAmount");
                f.filletHunger = config.getInt(fp + ".hunger");
                f.filletSaturation = config.getDouble(fp + ".saturation");
                f.filletCustomModelData = config.getInt(fp + ".customModelData");
                f.minFreshness = config.getInt(fp + ".minFreshness");
                f.maxFreshness = config.getInt(fp + ".maxFreshness");

                if (config.contains(fp + ".potionEffects")) {
                    for (Map<?, ?> map : config.getMapList(fp + ".potionEffects")) {
                        PotionEffectType type =
                                PotionEffectType.getByName((String) map.get("type"));
                        if (type != null) {
                            f.potionEffects.add(new PotionEffect(
                                    type,
                                    (int) map.get("duration"),
                                    (int) map.get("amplifier")
                            ));
                        }
                    }
                }
            }
            fishes.add(f);
        }
    }

    // ==================================================
    // 保存
    // ==================================================
    public void saveFishData() {
        config.set("fishes", null);

        for (int i = 0; i < fishes.size(); i++) {
            CustomFish f = fishes.get(i);
            String p = "fishes.fish" + i;

            config.set(p + ".name", f.name);
            config.set(p + ".probability", f.probability);
            config.set(p + ".rarity", f.rarity);
            config.set(p + ".lore", f.lore);
            config.set(p + ".minSize", f.minSize);
            config.set(p + ".maxSize", f.maxSize);
            config.set(p + ".customModelData", f.customModelData);

            if (f.filletingEnabled) {
                String fp = p + ".filleting";
                config.set(fp + ".enabled", true);
                config.set(fp + ".name", f.filletName);
                config.set(fp + ".minAmount", f.filletMinAmount);
                config.set(fp + ".maxAmount", f.filletMaxAmount);
                config.set(fp + ".hunger", f.filletHunger);
                config.set(fp + ".saturation", f.filletSaturation);
                config.set(fp + ".customModelData", f.filletCustomModelData);
                config.set(fp + ".minFreshness", f.minFreshness);
                config.set(fp + ".maxFreshness", f.maxFreshness);
            }
        }

        try {
            config.save(fishDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("fish_data.yml 保存失敗: " + e.getMessage());
        }
    }

    // ==================================================
    // ★ アイテム判定（static）
    // ==================================================
    public static boolean isCustomFishItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        for (String l : meta.getLore()) {
            if (l.startsWith("§8§l[ID:")) return true;
        }
        return false;
    }

    public static boolean isFilletItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        for (String l : meta.getLore()) {
            if (l.startsWith("§8§l[FILLET:")) return true;
        }
        return false;
    }

    // ==================================================
    // 売却価格計算（核心）
    // ==================================================
    public int getSellPrice(ItemStack item) {

        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return 0;

        String fishId = null;
        int size = 0;
        int freshness = 100;
        boolean fillet = false;

        for (String l : meta.getLore()) {
            if (l.startsWith("§8§l[ID:")) {
                fishId = l.replace("§8§l[ID:", "").replace("]", "");
            }
            if (l.startsWith("§8§l[FILLET:")) {
                fishId = l.replace("§8§l[FILLET:", "").replace("]", "");
                fillet = true;
            }
            if (l.startsWith("§7大きさ: ")) {
                size = Integer.parseInt(
                        l.replace("§7大きさ: ", "").replace("cm", "")
                );
            }
            if (l.startsWith("§8§l[FRESH:")) {
                freshness = Integer.parseInt(
                        l.replace("§8§l[FRESH:", "").replace("]", "")
                );
            }
        }

        if (fishId == null) return 0;

        CustomFish fish = getFishByName(fishId);
        if (fish == null) return 0;

        double base = fillet ? 50 : 100;
        double rarity = getRarityMultiplier(fish.rarity);
        double sizeMul = size > 0 ? (double) size / fish.maxSize : 1.0;
        double freshMul = freshness / 100.0;

        return Math.max(1, (int) (base * rarity * sizeMul * freshMul));
    }

    // ==================================================
    // 補助
    // ==================================================
    public CustomFish getFishByName(String name) {
        for (CustomFish f : fishes) {
            if (f.name.equalsIgnoreCase(name)) return f;
        }
        return null;
    }

    private double getRarityMultiplier(String rarity) {
        return switch (rarity) {
            case "COMMON" -> 1.0;
            case "UNCOMMON" -> 1.5;
            case "RARE" -> 2.5;
            case "EPIC" -> 4.0;
            case "LEGENDARY" -> 8.0;
            default -> 1.0;
        };
    }
}
