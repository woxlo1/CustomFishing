package Ora000.customfishing.menu.itemindex;

import Ora000.customfishing.Customfishing;
import Ora000.customfishing.data.FishDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RaritySelectMenu {

    private final Customfishing plugin;
    private final Inventory inv;

    public RaritySelectMenu(Customfishing plugin) {
        this.plugin = plugin;
        this.inv = Bukkit.createInventory(null, 54, "§b§l釣り図鑑 - レアリティ選択");
        build();
    }

    private void build() {
        // ===== 周囲デコレーション =====
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, glass(Material.GRAY_STAINED_GLASS_PANE));
        }

        // ===== レアリティボタン =====
        FishDataManager manager = plugin.getFishDataManager();

        int[] slots = {20, 21, 22, 23, 24};

        int i = 0;
        for (FishRarity rarity : FishRarity.values()) {

            long count = manager.getFishByRarity(rarity.name()).size();
            if (count == 0) continue;

            ItemStack it = new ItemStack(rarity.icon);
            ItemMeta meta = it.getItemMeta();

            meta.setDisplayName("§l" + rarity.display);

            List<String> lore = new ArrayList<>();
            lore.add("§7このレアリティの魚を見る");
            lore.add("");
            lore.add("§7登録数: §f" + count + "種");
            lore.add("");
            lore.add("§e▶ クリック");

            meta.setLore(lore);
            it.setItemMeta(meta);

            inv.setItem(slots[i++], it);
        }

        // ===== 戻る =====
        inv.setItem(49,
                button(Material.BARRIER, "§c閉じる"));
    }

    public Inventory getInventory() {
        return inv;
    }

    // ==================================================
    // util
    // ==================================================
    private ItemStack glass(Material m) {
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(" ");
        it.setItemMeta(meta);
        return it;
    }

    private ItemStack button(Material m, String name) {
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name);
        it.setItemMeta(meta);
        return it;
    }
}
