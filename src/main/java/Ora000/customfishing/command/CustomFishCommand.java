package Ora000.customfishing.command;

import Ora000.customfishing.Customfishing;
import Ora000.customfishing.data.FishDataManager;
import Ora000.customfishing.menu.SellFishMenu;
import Ora000.customfishing.model.CustomFish;
import Ora000.customfishing.util.CustomFishingRod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomFishCommand implements CommandExecutor {

    private final Customfishing plugin;
    private final FishDataManager data;

    public CustomFishCommand(Customfishing plugin, FishDataManager data) {
        this.plugin = plugin;
        this.data = data;
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] a) {

        // ===============================
        // help
        // ===============================
        if (a.length == 0) {
            s.sendMessage(Customfishing.PREFIX + "§6=== カスタム釣り ===");
            s.sendMessage("§e/customfish list");
            s.sendMessage("§e/customfish sell");

            if (s.hasPermission("customfishing.admin")) {
                s.sendMessage("§c=== 管理者 ===");
                s.sendMessage("§e/customfish add <name> <price> <material> <lore> <min> <max> <chance>");
                s.sendMessage("§e/customfish remove <番号>");
                s.sendMessage("§e/customfish reload");
                s.sendMessage("§e/customfish rod create | remove");
                s.sendMessage("§e/customfish on / off");
            }
            return true;
        }

        // ===============================
        // sell
        // ===============================
        if (a[0].equalsIgnoreCase("sell")) {

            if (!s.hasPermission("customfishing.sell")) {
                s.sendMessage(Customfishing.PREFIX + "§c権限がありません。");
                return true;
            }

            if (!(s instanceof Player p)) {
                s.sendMessage("§cプレイヤー専用です。");
                return true;
            }

            boolean hasFish = false;
            for (ItemStack it : p.getInventory().getContents()) {
                if (it == null) continue;

                if (FishDataManager.isCustomFishItem(it)
                        || FishDataManager.isFilletItem(it)) {
                    hasFish = true;
                    break;
                }
            }

            if (!hasFish) {
                p.sendMessage(Customfishing.PREFIX + "§c売却できる魚がありません。");
                return true;
            }

            p.openInventory(new SellFishMenu(plugin, p).getInventory());
            return true;
        }

        // ===============================
        // list
        // ===============================
        if (a[0].equalsIgnoreCase("list")) {

            if (!s.hasPermission("customfishing.list")) {
                s.sendMessage(Customfishing.PREFIX + "§c権限がありません。");
                return true;
            }

            if (data.fishes.isEmpty()) {
                s.sendMessage(Customfishing.PREFIX + "§7魚なし");
                return true;
            }

            s.sendMessage(Customfishing.PREFIX + "§6=== 魚一覧 ===");
            for (int i = 0; i < data.fishes.size(); i++) {
                CustomFish f = data.fishes.get(i);
                s.sendMessage("§e[" + i + "] " + c(f.name));
            }
            return true;
        }

        // ===============================
        // 管理者チェック
        // ===============================
        if (!s.hasPermission("customfishing.admin")) {
            s.sendMessage(Customfishing.PREFIX + "§c管理者権限がありません。");
            return true;
        }

        // ===============================
        // add
        // ===============================
        if (a[0].equalsIgnoreCase("add") && a.length >= 8) {
            try {
                data.fishes.add(new CustomFish(
                        a[1],
                        Double.parseDouble(a[2]),
                        a[3],
                        a[4],
                        Integer.parseInt(a[5]),
                        Integer.parseInt(a[6]),
                        Integer.parseInt(a[7])
                ));
                data.saveFishData();
                s.sendMessage(Customfishing.PREFIX + "§a追加しました。");
            } catch (Exception e) {
                s.sendMessage(Customfishing.PREFIX + "§c数値エラー！");
            }
            return true;
        }

        // ===============================
        // remove
        // ===============================
        if (a[0].equalsIgnoreCase("remove") && a.length >= 2) {
            try {
                int i = Integer.parseInt(a[1]);
                if (i >= 0 && i < data.fishes.size()) {
                    data.fishes.remove(i);
                    data.saveFishData();
                    s.sendMessage(Customfishing.PREFIX + "§a削除しました。");
                } else {
                    s.sendMessage(Customfishing.PREFIX + "§c無効な番号");
                }
            } catch (NumberFormatException e) {
                s.sendMessage(Customfishing.PREFIX + "§c番号を指定してください。");
            }
            return true;
        }

        // ===============================
        // reload
        // ===============================
        if (a[0].equalsIgnoreCase("reload")) {
            data.loadFishData();
            s.sendMessage(Customfishing.PREFIX + "§aリロード完了");
            return true;
        }

        // ===============================
        // rod
        // ===============================
        if (a[0].equalsIgnoreCase("rod") && a.length >= 2) {

            if (!(s instanceof Player p)) return true;

            ItemStack hand = p.getInventory().getItemInMainHand();
            if (hand.getType().isAir()) {
                p.sendMessage(Customfishing.PREFIX + "§c釣り竿を持ってください。");
                return true;
            }

            if (a[1].equalsIgnoreCase("create")) {
                CustomFishingRod.addRodNBT(plugin, hand);
                p.sendMessage(Customfishing.PREFIX + "§a専用釣り竿にしました。");
                return true;
            }

            if (a[1].equalsIgnoreCase("remove")) {
                CustomFishingRod.removeRodNBT(plugin, hand);
                p.sendMessage(Customfishing.PREFIX + "§c専用釣り竿を解除しました。");
                return true;
            }
        }

        // ===============================
        // on / off
        // ===============================
        if (a[0].equalsIgnoreCase("on")) {
            plugin.setFishingEnabled(true);
            s.sendMessage(Customfishing.PREFIX + "§aカスタム釣りを有効化しました。");
            return true;
        }

        if (a[0].equalsIgnoreCase("off")) {
            plugin.setFishingEnabled(false);
            s.sendMessage(Customfishing.PREFIX + "§cカスタム釣りを無効化しました。");
            return true;
        }

        return true;
    }
}
