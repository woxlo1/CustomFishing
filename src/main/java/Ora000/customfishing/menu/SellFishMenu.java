package Ora000.customfishing.menu;

import Ora000.customfishing.Customfishing;
import Ora000.customfishing.data.FishDataManager;
import Ora000.customfishing.util.SellLogger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class SellFishMenu implements InventoryHolder, Listener {

    private final Customfishing plugin;
    private final Player player;
    private final Inventory inv;
    private boolean selling = false; // 二重実行防止

    public SellFishMenu(Customfishing plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inv = Bukkit.createInventory(this, 36, "§a魚売却メニュー");

        loadItems();

        // ✅ Listener登録（このGUI専用）
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void loadItems() {
        inv.clear();

        int totalPrice = 0;

        // 魚表示 & 合計計算
        for (ItemStack it : player.getInventory().getContents()) {
            if (it == null) continue;
            if (FishDataManager.isCustomFishItem(it) || FishDataManager.isFilletItem(it)) {
                inv.addItem(it.clone());
                totalPrice += plugin.getDataManager().getSellPrice(it) * it.getAmount();
            }
        }

        // 下段装飾
        for (int i = 27; i < 36; i++) {
            ItemStack pane = new ItemStack(i == 31
                    ? Material.LIME_STAINED_GLASS_PANE
                    : Material.GRAY_STAINED_GLASS_PANE);

            ItemMeta meta = pane.getItemMeta();
            meta.setDisplayName(i == 31 ? "§c§l売却する!!" : " ");

            if (i == 31) {
                meta.setLore(java.util.Collections.singletonList("§a§l" + totalPrice + "円で売る"));
            }

            pane.setItemMeta(meta);
            inv.setItem(i, pane);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;
        e.setCancelled(true);

        if (e.getSlot() != 31) return;
        if (selling) return; // 二重防止

        selling = true;

        int totalPrice = 0;
        int count = 0;
        StringBuilder detail = new StringBuilder();

        for (ItemStack it : player.getInventory().getContents()) {
            if (it == null) continue;

            if (FishDataManager.isCustomFishItem(it)
                    || FishDataManager.isFilletItem(it)) {

                int price = plugin.getDataManager().getSellPrice(it);
                if (price <= 0) continue; // Lore改ざん対策

                totalPrice += price * it.getAmount();
                count += it.getAmount();
                detail.append(it.getAmount()).append("x").append(it.getType()).append(" ");

                player.getInventory().remove(it);
            }
        }

        if (totalPrice <= 0) {
            player.sendMessage(Customfishing.PREFIX + "§c売却できる魚がありません。");
            selling = false;
            player.closeInventory();
            return;
        }

        // ✅ Vault加算
        plugin.getVaultManager().deposit(player.getUniqueId(), totalPrice);

        // ✅ ログ
        SellLogger.log(player, totalPrice, count, detail.toString());

        player.sendMessage(Customfishing.PREFIX + "§a§l魚を§e§l" + totalPrice + "円§a§lで売却しました！");

        player.closeInventory();
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
