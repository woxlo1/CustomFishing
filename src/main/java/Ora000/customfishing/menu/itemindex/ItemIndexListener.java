package Ora000.customfishing.menu.itemindex;

import Ora000.customfishing.Customfishing;
import Ora000.customfishing.model.CustomFish;
import Ora000.customfishing.data.PlayerCatchData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemIndexListener implements Listener {

    private final Customfishing plugin;

    public ItemIndexListener(Customfishing plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        ItemStack item = e.getCurrentItem();

        if (item == null || !item.hasItemMeta()) return;

        e.setCancelled(true);

        ItemMeta meta = item.getItemMeta();
        String name = meta.getDisplayName();

        // ===============================
        // 図鑑トップ → レアリティ
        // ===============================
        if (title.equals("§9§l釣り図鑑")) {
            for (FishRarity r : FishRarity.values()) {
                if (name.contains(r.display)) {
                    p.openInventory(new FishListMenu(plugin, r).getInventory());
                    return;
                }
            }
        }

        // ===============================
        // レアリティ → 魚一覧
        // ===============================
        if (title.startsWith("§b§l図鑑 - ")) {

            if (name.equals("§c戻る")) {
                p.openInventory(new ItemIndexMenu(plugin).getInventory());
                return;
            }

            CustomFish cf = plugin.getDataManager().getFishByName(strip(name));
            if (cf == null) return;

            PlayerCatchData pdata = plugin.getPlayerFishData();

            if (pdata.getCount(p.getUniqueId(), cf.name) <= 0) {
                p.sendMessage("§cこの魚はまだ釣っていません！");
                return;
            }

            p.openInventory(new FishDetailMenu(plugin, p, cf).getInventory());
            return;
        }

        // ===============================
        // 詳細 → 戻る
        // ===============================
        if (title.startsWith("§6§l図鑑詳細")) {
            if (name.equals("§c戻る")) {
                p.openInventory(new ItemIndexMenu(plugin).getInventory());
            }
        }
    }

    private String strip(String s) {
        return s.replace("§e§l", "").replace("§e", "");
    }
}
