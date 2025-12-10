package Ora000.customfishing.data;

import Ora000.customfishing.model.CustomFish;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FishDataManager {

    private final JavaPlugin plugin;
    private final File fishDataFile;
    private FileConfiguration config;

    public final List<CustomFish> fishes = new ArrayList<>();

    // ==================================================
    // constructor
    // ==================================================
    public FishDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.fishDataFile = new File(plugin.getDataFolder(), "fish_data.yml");

        if (!fishDataFile.exists()) {
            try {
                fishDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("fish_data.yml 作成失敗: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(fishDataFile);
    }

    // ==================================================
    // load
    // ==================================================
    public void loadFishData() {
        fishes.clear();
        config = YamlConfiguration.loadConfiguration(fishDataFile);

        if (!config.contains("fishes")) return;

        for (String key : config.getConfigurationSection("fishes").getKeys(false)) {
            String p = "fishes." + key;

            Material material = Material.matchMaterial(
                    config.getString(p + ".material", "COD"));

            CustomFish f = new CustomFish(
                    config.getString(p + ".name"),
                    config.getDouble(p + ".probability"),
                    config.getString(p + ".rarity"),
                    config.getString(p + ".lore"),
                    config.getInt(p + ".minSize"),
                    config.getInt(p + ".maxSize"),
                    config.getInt(p + ".customModelData")
            );

            // ===== filleting =====
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

                // potion effects
                f.potionEffects.clear();
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
            fishes.add(f);
        }
    }

    // ==================================================
    // save
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

            // ===== filleting =====
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

                // ✅ potionEffects 保存（重要）
                List<Map<String, Object>> list = new ArrayList<>();
                for (PotionEffect pe : f.potionEffects) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("type", pe.getType().getName());
                    m.put("duration", pe.getDuration());
                    m.put("amplifier", pe.getAmplifier());
                    list.add(m);
                }
                config.set(fp + ".potionEffects", list);
            }
        }

        try {
            config.save(fishDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("fish_data.yml 保存失敗: " + e.getMessage());
        }
    }

    // ==================================================
    // item check
    // ==================================================
    public static boolean isCustomFishItem(ItemStack item) {
        return hasLoreKey(item, "§8§l[ID:");
    }

    public static boolean isFilletItem(ItemStack item) {
        return hasLoreKey(item, "§8§l[FILLET:");
    }

    private static boolean hasLoreKey(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;
        return meta.getLore().stream().anyMatch(l -> l.startsWith(key));
    }

    // ==================================================
    // sell price
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
            if (l.startsWith("§8§l[ID:")) fishId = extract(l);
            if (l.startsWith("§8§l[FILLET:")) {
                fishId = extract(l);
                fillet = true;
            }
            if (l.startsWith("§7大きさ: "))
                size = Integer.parseInt(l.replace("§7大きさ: ", "").replace("cm", ""));
            if (l.startsWith("§8§l[FRESH:"))
                freshness = Integer.parseInt(l.replace("§8§l[FRESH:", "").replace("]", ""));
        }

        CustomFish fish = getFishByName(fishId);
        if (fish == null) return 0;

        double base = fillet ? 50 : 100;
        double price =
                base *
                        getRarityMultiplier(fish.rarity) *
                        (size > 0 ? (double) size / fish.maxSize : 1.0) *
                        (freshness / 100.0);

        return Math.max(1, (int) price);
    }

    private String extract(String l) {
        return l.substring(l.indexOf(":") + 1, l.length() - 1);
    }

    // ==================================================
    // utility
    // ==================================================
    public CustomFish getFishByName(String name) {
        if (name == null) return null;
        for (CustomFish f : fishes) {
            if (f.name.equalsIgnoreCase(name)) return f;
        }
        return null;
    }

    private double getRarityMultiplier(String rarity) {
        return switch (rarity.toUpperCase()) {
            case "COMMON" -> 1.0;
            case "UNCOMMON" -> 1.5;
            case "RARE" -> 2.5;
            case "EPIC" -> 4.0;
            case "LEGENDARY" -> 8.0;
            default -> 1.0;
        };
    }

    // ==================================================
    // ★ 図鑑用 API
    // ==================================================
    public Set<String> getAllRarities() {
        Set<String> set = new LinkedHashSet<>();
        for (CustomFish f : fishes) set.add(f.rarity.toUpperCase());
        return set;
    }

    public List<CustomFish> getFishByRarity(String rarity) {
        List<CustomFish> list = new ArrayList<>();
        for (CustomFish f : fishes) {
            if (f.rarity.equalsIgnoreCase(rarity)) list.add(f);
        }
        return list;
    }
}
