package Ora000.customfishing.menu.itemindex;

import Ora000.customfishing.Customfishing;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemIndexMenu {

    private final Customfishing plugin;
    private final Inventory inv;

    public ItemIndexMenu(Customfishing plugin) {
        this.plugin = plugin;
        this.inv = Bukkit.createInventory(null, 54, "§9§l釣り図鑑");
        build();
    }

    private void build() {
        IndexMenuUtil.fill(inv, org.bukkit.Material.BLACK_STAINED_GLASS_PANE);

        int[] slots = {20, 21, 22, 23, 24};

        int i = 0;
        for (FishRarity r : FishRarity.values()) {
            ItemStack it = IndexMenuUtil.button(
                    r.icon,
                    "§e§l" + r.display
            );
            inv.setItem(slots[i++], it);
        }
    }

    public Inventory getInventory() {
        return inv;
    }
}
