package Ora000.customfishing.menu.itemindex;

import Ora000.customfishing.Customfishing;
import Ora000.customfishing.data.PlayerCatchData;
import Ora000.customfishing.model.CustomFish;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FishListMenu {

    private final Customfishing plugin;
    private final Inventory inv;
    private final FishRarity rarity;

    public FishListMenu(Customfishing plugin, FishRarity rarity) {
        this.plugin = plugin;
        this.rarity = rarity;
        this.inv = Bukkit.createInventory(
                null,
                54,
                "§b§l図鑑 - " + rarity.display
        );
        build();
    }

    private void build() {

        // ===============================
        // 装飾
        // ===============================
        IndexMenuUtil.fill(inv, Material.BLUE_STAINED_GLASS_PANE);

        PlayerCatchData pdata = plugin.getPlayerFishData();

        int slot = 10;

        for (CustomFish cf : plugin.getDataManager().fishes) {

            if (!cf.rarity.equalsIgnoreCase(rarity.name())) continue;

            // ✅ 未所持判定（図鑑ロック）
            boolean unlocked = false;

            // Player は Listener 側で取得するため
            // 表示上は「未所持想定」で描画 → クリック時に制御
            // （個人別描画したい場合は InventoryHolder を使う）
            // → 実運用では Listener で最終判定
            unlocked = true; // 表示は解放前提（制御は Listener）

            ItemStack it = new ItemStack(Material.COD);
            ItemMeta meta = it.getItemMeta();
            List<String> lore = new ArrayList<>();

            if (unlocked) {
                // ===============================
                // 解放済み魚
                // ===============================
                meta.setDisplayName("§e§l" + cf.name);
                meta.setCustomModelData(cf.customModelData);

                lore.add("§7レアリティ: " + rarity.display);
                lore.add("§7サイズ範囲:");
                lore.add(" §a" + cf.minSize + "cm §7～ §c" + cf.maxSize + "cm");
                lore.add("");
                lore.add("§e▶ クリックで詳細を見る");

            } else {
                // ===============================
                // 未発見魚（？）
                // ===============================
                meta.setDisplayName("§7§l？？？");
                meta.setCustomModelData(0);

                lore.add("§8未発見の魚");
                lore.add("§8この魚を釣り上げると");
                lore.add("§8図鑑が解放されます");
            }

            meta.setLore(lore);
            it.setItemMeta(meta);

            inv.setItem(slot++, it);

            // 行折り返し制御
            if ((slot + 1) % 9 == 0) {
                slot += 2;
            }
        }

        // ===============================
        // 戻るボタン
        // ===============================
        inv.setItem(49, IndexMenuUtil.button(Material.BARRIER, "§c戻る"));
    }

    public Inventory getInventory() {
        return inv;
    }
}
