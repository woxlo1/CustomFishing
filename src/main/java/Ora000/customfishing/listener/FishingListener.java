package Ora000.customfishing.listener;

import Ora000.customfishing.Customfishing;
import Ora000.customfishing.data.FishDataManager;
import Ora000.customfishing.data.ServerRecordData;
import Ora000.customfishing.model.CustomFish;
import Ora000.customfishing.util.CustomFishingRod;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class FishingListener implements Listener {

    private final Customfishing plugin;
    private final FishDataManager data;

    public FishingListener(Customfishing plugin, FishDataManager data) {
        this.plugin = plugin;
        this.data = data;
    }

    /* ==================================================
     * 共通ユーティリティ
     * ================================================== */

    private String c(String t) {
        return ChatColor.translateAlternateColorCodes('&', t);
    }

    private String freshnessColor(int f) {
        if (f >= 90) return "§a";
        if (f >= 70) return "§e";
        if (f >= 50) return "§6";
        if (f >= 30) return "§c";
        return "§4";
    }

    private String freshnessRank(int f) {
        if (f >= 90) return "最高";
        if (f >= 70) return "良好";
        if (f >= 50) return "普通";
        if (f >= 30) return "低下";
        return "劣悪";
    }

    /* ==================================================
     * 釣り処理
     * ================================================== */
    @EventHandler
    public void onFish(PlayerFishEvent e) {

        // プラグインOFF
        if (!plugin.isFishingEnabled()) return;

        Player p = e.getPlayer();
        ItemStack rod = p.getInventory().getItemInMainHand();

        // ✅ 専用釣り竿以外は完全ブロック
        if (!CustomFishingRod.isRod(plugin, rod)) {
            e.setCancelled(true);

            if (e.getHook() != null) {
                e.getHook().remove();
            }

            p.sendMessage(Customfishing.PREFIX +
                    "§cこの釣りでは釣れません。専用釣り竿を使用してください。");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.8f);
            return;
        }

        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (data.fishes.isEmpty()) return;

        Random r = new Random();

        double total = data.fishes.stream().mapToDouble(f -> f.probability).sum();
        double roll = r.nextDouble() * 100;
        if (roll > total) return;

        double cur = 0;
        CustomFish cf = null;

        for (CustomFish f : data.fishes) {
            cur += f.probability;
            if (roll <= cur) {
                cf = f;
                break;
            }
        }
        if (cf == null) return;

        // VANILLA魚除去
        if (e.getCaught() instanceof Item it) it.remove();

        int size = cf.minSize + r.nextInt(Math.max(1, cf.maxSize - cf.minSize + 1));
        int freshness = cf.minFreshness +
                r.nextInt(Math.max(1, cf.maxFreshness - cf.minFreshness + 1));

        ItemStack fish = new ItemStack(Material.TROPICAL_FISH);
        ItemMeta m = fish.getItemMeta();

        String fColor = freshnessColor(freshness);
        String fRank = freshnessRank(freshness);

        m.setDisplayName(c(cf.name) + " §7(" + size + "cm)");
        List<String> lore = new ArrayList<>();
        lore.add("§7" + c(cf.lore));
        lore.add("§eレア度: " + c(cf.rarity));
        lore.add("§7大きさ: " + size + "cm");
        lore.add(fColor + "鮮度: " + freshness + " (" + fRank + ")");
        if (cf.filletingEnabled) lore.add("§a右クリックで捌ける！");
        lore.add("§8§l[ID:" + cf.name + "]");
        lore.add("§8§l[FRESH:" + freshness + "]");

        m.setLore(lore);
        if (cf.customModelData > 0) m.setCustomModelData(cf.customModelData);
        m.addEnchant(Enchantment.LURE, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        fish.setItemMeta(m);
        giveItem(p, fish);

        // サーバーレコード判定
        ServerRecordData.FishRecord record = plugin.getServerRecordData().getRecord(cf.name);
        boolean isMax = plugin.getServerRecordData().checkMaxRecord(cf.name, size, p.getName());
        boolean isMin = plugin.getServerRecordData().checkMinRecord(cf.name, size, p.getName());

        // Broadcastはここで行う
        if (isMax) {
            Bukkit.broadcastMessage(Customfishing.PREFIX +
                    "§6新記録！ §e" + p.getName() + " が " + cf.name + " " + size + "cm を釣りました！（最大）");
        }

        if (isMin) {
            Bukkit.broadcastMessage(Customfishing.PREFIX +
                    "§7最小記録… §e" + p.getName() + " が " + cf.name + " " + size + "cm を釣りました。（最小）");
        }

        plugin.getAntiMacroManager().onFishing(p);
        p.sendMessage(Customfishing.PREFIX +
                "§a§l釣れた！ §r" + m.getDisplayName() +
                " " + fColor + "[" + fRank + "]");
    }

    /* ==================================================
     * 捌く
     * ================================================== */
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_AIR &&
                e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player p = e.getPlayer();
        ItemStack it = p.getInventory().getItemInMainHand();
        if (it == null || it.getType() != Material.TROPICAL_FISH || !it.hasItemMeta()) return;

        ItemMeta m = it.getItemMeta();
        if (!m.hasLore()) return;

        String id = null;
        int size = 0;
        int freshness = 50;

        for (String l : m.getLore()) {
            if (l.startsWith("§8§l[ID:"))
                id = l.replace("§8§l[ID:", "").replace("]", "");
            if (l.startsWith("§8§l[FRESH:"))
                freshness = Integer.parseInt(l.replace("§8§l[FRESH:", "").replace("]", ""));
            if (l.startsWith("§7大きさ: "))
                size = Integer.parseInt(l.replace("§7大きさ: ", "").replace("cm", ""));
        }

        if (id == null) return;

        String finalId = id;
        CustomFish cf = data.fishes.stream()
                .filter(f -> f.name.equals(finalId))
                .findFirst().orElse(null);

        if (cf == null || !cf.filletingEnabled) {
            p.sendMessage(Customfishing.PREFIX + "§cこの魚は捌けません！");
            return;
        }

        e.setCancelled(true);
        it.setAmount(it.getAmount() - 1);

        Random r = new Random();
        int amt = cf.filletMinAmount +
                r.nextInt(Math.max(1, cf.filletMaxAmount - cf.filletMinAmount + 1));

        amt += (int)((double) size / Math.max(1, cf.maxSize) * 2);
        if (amt < 1) amt = 1;

        ItemStack fillet = new ItemStack(Material.COOKED_SALMON, amt);
        ItemMeta fm = fillet.getItemMeta();

        String fColor = freshnessColor(freshness);
        String fRank = freshnessRank(freshness);

        fm.setDisplayName(c(cf.filletName) + " " + fColor + "[" + fRank + "]");
        List<String> lore = new ArrayList<>();
        lore.add("§7" + c(cf.name) + "§7から取れた身");
        lore.add("§e満腹度: §f+" + cf.filletHunger +
                " §7(+" + cf.filletSaturation + ")");
        lore.add(fColor + "鮮度: " + freshness);
        lore.add("§8§l[FILLET:" + cf.name + "]");
        lore.add("§8§l[FRESH:" + freshness + "]");

        fm.setLore(lore);
        if (cf.filletCustomModelData > 0)
            fm.setCustomModelData(cf.filletCustomModelData);

        fillet.setItemMeta(fm);
        giveItem(p, fillet);

        p.sendMessage(Customfishing.PREFIX +
                "§a§l捌いた！ §r" + c(cf.filletName) +
                " " + fColor + "[" + fRank + "] §7x" + amt);
        p.playSound(p.getLocation(), Sound.ITEM_AXE_STRIP, 1f, 1f);
    }

    /* ==================================================
     * 食べる
     * ================================================== */
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {

        ItemStack it = e.getItem();
        if (!it.hasItemMeta() || !it.getItemMeta().hasLore()) return;

        String id = null;
        int freshness = 50;

        for (String l : it.getItemMeta().getLore()) {
            if (l.startsWith("§8§l[FILLET:"))
                id = l.replace("§8§l[FILLET:", "").replace("]", "");
            if (l.startsWith("§8§l[FRESH:"))
                freshness = Integer.parseInt(l.replace("§8§l[FRESH:", "").replace("]", ""));
        }

        if (id == null) return;

        String finalId = id;
        CustomFish cf = data.fishes.stream()
                .filter(f -> f.name.equals(finalId))
                .findFirst().orElse(null);

        if (cf == null || cf.potionEffects.isEmpty()) return;

        Player p = e.getPlayer();

        for (PotionEffect ef : cf.potionEffects) {
            int d = Math.max(1, (int) (ef.getDuration() * freshness / 100.0));
            p.addPotionEffect(new PotionEffect(
                    ef.getType(), d, ef.getAmplifier()));
        }

        p.sendMessage(Customfishing.PREFIX +
                "§d§l効果発動！ " +
                freshnessColor(freshness) + "[鮮度:" + freshness + "]");
        p.playSound(p.getLocation(),
                Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2f);
    }

    /* ==================================================
     * インベントリ付与
     * ================================================== */
    private void giveItem(Player p, ItemStack item) {
        PlayerInventory inv = p.getInventory();
        HashMap<Integer, ItemStack> rest = inv.addItem(item);
        for (ItemStack r : rest.values()) {
            p.getWorld().dropItemNaturally(p.getLocation(), r);
        }
    }
}
