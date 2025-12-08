package Ora000.customfishing.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CustomFishingRod {

    public static final String KEY = "custom_fishing_rod";

    public static boolean isRod(JavaPlugin plugin, ItemStack it) {
        if (it == null || it.getType() != Material.FISHING_ROD) return false;
        if (!it.hasItemMeta()) return false;
        return it.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(plugin, KEY), PersistentDataType.BYTE);
    }

    public static void addRodNBT(JavaPlugin plugin, ItemStack it) {
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§b§lカスタムフィッシングロッド");
        meta.setLore(List.of(
                "§7この釣り竿でのみ",
                "§7カスタム魚が釣れる"
        ));
        meta.setCustomModelData(10001);

        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, KEY),
                PersistentDataType.BYTE,
                (byte) 1
        );
        it.setItemMeta(meta);
    }

    public static void removeRodNBT(JavaPlugin plugin, ItemStack it) {
        if (!it.hasItemMeta()) return;
        ItemMeta meta = it.getItemMeta();
        meta.getPersistentDataContainer().remove(
                new NamespacedKey(plugin, KEY)
        );
        it.setItemMeta(meta);
    }
}
