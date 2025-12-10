package Ora000.customfishing.menu.itemindex;

import Ora000.customfishing.Customfishing;
import Ora000.customfishing.model.CustomFish;
import Ora000.customfishing.data.ServerRecordData.FishRecord;
import Ora000.customfishing.data.PlayerCatchData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FishDetailMenu {

    private final Inventory inv;

    public FishDetailMenu(Customfishing plugin, Player p, CustomFish cf) {
        this.inv = Bukkit.createInventory(null, 54, "§6§l図鑑詳細 - " + cf.name);
        build(plugin, p, cf);
    }

    private void build(Customfishing plugin, Player p, CustomFish cf) {
        IndexMenuUtil.fill(inv, Material.GRAY_STAINED_GLASS_PANE);

        // ✅ 図鑑用アイコン（material は固定）
        ItemStack fish = new ItemStack(Material.COD);
        ItemMeta meta = fish.getItemMeta();

        meta.setDisplayName("§e§l" + cf.name);
        meta.setCustomModelData(cf.customModelData);

        List<String> lore = new ArrayList<>();

        // ===============================
        // サーバーレコード
        // ===============================
        FishRecord record = plugin.getServerRecordData().getRecord(cf.name);

        lore.add("§6§l== サーバーレコード ==");
        if (record.hasMax()) {
            lore.add("§e最大: §a" + record.maxSize + "cm");
            lore.add("§7釣人: §f" + record.maxOwner);
        } else {
            lore.add("§7最大: 未記録");
        }

        lore.add("");

        if (record.hasMin()) {
            lore.add("§e最小: §c" + record.minSize + "cm");
            lore.add("§7釣人: §f" + record.minOwner);
        } else {
            lore.add("§7最小: 未記録");
        }

        lore.add("");

        // ===============================
        // 個人データ
        // ===============================
        PlayerCatchData pdata = plugin.getPlayerFishData();

        int count = pdata.getCount(p.getUniqueId(), cf.name);
        int max = pdata.getMax(p.getUniqueId(), cf.name);
        int min = pdata.getMin(p.getUniqueId(), cf.name);

        lore.add("§b§l== あなたの記録 ==");
        lore.add("§7釣った数: §f" + count + "回");

        if (count > 0) {
            lore.add("§7最大: §a" + max + "cm");
            lore.add("§7最小: §c" + min + "cm");
        } else {
            lore.add("§7未入手");
        }

        meta.setLore(lore);
        fish.setItemMeta(meta);

        inv.setItem(22, fish);
        inv.setItem(49, IndexMenuUtil.button(Material.BARRIER, "§c戻る"));
    }

    public Inventory getInventory() {
        return inv;
    }
}
