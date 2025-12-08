package Ora000.customfishing.command;

import Ora000.customfishing.Customfishing;
import Ora000.customfishing.data.FishDataManager;
import Ora000.customfishing.model.CustomFish;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FilletingCommand implements CommandExecutor {

    private final Customfishing plugin;
    private final FishDataManager data;

    public FilletingCommand(Customfishing plugin, FishDataManager data) {
        this.plugin = plugin;
        this.data = data;
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] a) {

        // ===============================
        // 権限チェック（最優先）
        // ===============================
        if (!s.hasPermission("customfishing.filleting.admin")) {
            s.sendMessage(Customfishing.PREFIX + "§cこのコマンドを実行する権限がありません。");
            return true;
        }

        // ===============================
        // ヘルプ
        // ===============================
        if (a.length == 0) {
            s.sendMessage(Customfishing.PREFIX + "§6=== 魚の捌き設定コマンド ===");
            s.sendMessage("§e/filleting set <魚番号> <身の名前> <最小数> <最大数> <満腹度> <隠し満腹度> <CMD> <最小鮮度> <最大鮮度>");
            s.sendMessage("§e/filleting addpotion <魚番号> <効果名> <秒数> <レベル>");
            s.sendMessage("§e/filleting clearpotion <魚番号>");
            s.sendMessage("§e/filleting remove <魚番号>");
            s.sendMessage("§e/filleting info <魚番号>");
            return true;
        }

        // ===============================
        // set
        // ===============================
        if (a[0].equalsIgnoreCase("set")) {
            if (a.length < 8) {
                s.sendMessage(Customfishing.PREFIX + "§c使い方: /filleting set <魚番号> <身の名前> <最小数> <最大数> <満腹度> <隠し満腹度> <CMD> [最小鮮度] [最大鮮度]");
                return true;
            }
            try {
                int i = Integer.parseInt(a[1]);
                if (i < 0 || i >= data.fishes.size()) {
                    s.sendMessage(Customfishing.PREFIX + "§c無効な魚番号です！");
                    return true;
                }
                CustomFish f = data.fishes.get(i);
                f.filletingEnabled = true;
                f.filletName = a[2];
                f.filletMinAmount = Integer.parseInt(a[3]);
                f.filletMaxAmount = Integer.parseInt(a[4]);
                f.filletHunger = Integer.parseInt(a[5]);
                f.filletSaturation = Double.parseDouble(a[6]);
                f.filletCustomModelData = Integer.parseInt(a[7]);

                if (a.length >= 9)
                    f.minFreshness = Math.max(0, Math.min(100, Integer.parseInt(a[8])));
                if (a.length >= 10)
                    f.maxFreshness = Math.max(0, Math.min(100, Integer.parseInt(a[9])));

                data.saveFishData();
                s.sendMessage(Customfishing.PREFIX + "§a捌き設定を追加しました！");
            } catch (NumberFormatException e) {
                s.sendMessage(Customfishing.PREFIX + "§c数値の形式が正しくありません！");
            }
            return true;
        }

        // ===============================
        // addpotion
        // ===============================
        if (a[0].equalsIgnoreCase("addpotion") && a.length >= 5) {
            try {
                int i = Integer.parseInt(a[1]);
                if (i < 0 || i >= data.fishes.size()) {
                    s.sendMessage(Customfishing.PREFIX + "§c無効な魚番号！");
                    return true;
                }
                CustomFish f = data.fishes.get(i);
                if (!f.filletingEnabled) {
                    s.sendMessage(Customfishing.PREFIX + "§c先に捌き設定を追加してください！");
                    return true;
                }
                PotionEffectType type = PotionEffectType.getByName(a[2].toUpperCase());
                if (type == null) {
                    s.sendMessage(Customfishing.PREFIX + "§c無効な効果名！");
                    return true;
                }
                int sec = Integer.parseInt(a[3]);
                int lv = Integer.parseInt(a[4]);
                f.potionEffects.add(new PotionEffect(type, sec * 20, lv - 1));
                data.saveFishData();
                s.sendMessage(Customfishing.PREFIX + "§a効果を追加しました！");
            } catch (NumberFormatException e) {
                s.sendMessage(Customfishing.PREFIX + "§c数値の形式が正しくありません！");
            }
            return true;
        }

        // ===============================
        // clearpotion
        // ===============================
        if (a[0].equalsIgnoreCase("clearpotion") && a.length >= 2) {
            try {
                int i = Integer.parseInt(a[1]);
                if (i >= 0 && i < data.fishes.size()) {
                    data.fishes.get(i).potionEffects.clear();
                    data.saveFishData();
                    s.sendMessage(Customfishing.PREFIX + "§a効果を削除しました！");
                } else {
                    s.sendMessage(Customfishing.PREFIX + "§c無効な番号");
                }
            } catch (NumberFormatException e) {
                s.sendMessage(Customfishing.PREFIX + "§c数値エラー");
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
                    data.fishes.get(i).filletingEnabled = false;
                    data.saveFishData();
                    s.sendMessage(Customfishing.PREFIX + "§a捌き設定を削除しました！");
                } else {
                    s.sendMessage(Customfishing.PREFIX + "§c無効な番号");
                }
            } catch (NumberFormatException e) {
                s.sendMessage(Customfishing.PREFIX + "§c数値エラー");
            }
            return true;
        }

        // ===============================
        // info
        // ===============================
        if (a[0].equalsIgnoreCase("info") && a.length >= 2) {
            try {
                int i = Integer.parseInt(a[1]);
                if (i >= 0 && i < data.fishes.size()) {
                    CustomFish f = data.fishes.get(i);
                    s.sendMessage(Customfishing.PREFIX + "§6=== " + c(f.name) + " ===");
                    if (f.filletingEnabled) {
                        s.sendMessage("§e身: §f" + c(f.filletName));
                        s.sendMessage("§e数: §f" + f.filletMinAmount + "～" + f.filletMaxAmount);
                        s.sendMessage("§e満腹: §f" + f.filletHunger + " (+" + f.filletSaturation + ")");
                        s.sendMessage("§e鮮度: §f" + f.minFreshness + "～" + f.maxFreshness);
                    } else {
                        s.sendMessage("§c捌き設定なし");
                    }
                } else {
                    s.sendMessage(Customfishing.PREFIX + "§c無効な番号");
                }
            } catch (NumberFormatException e) {
                s.sendMessage(Customfishing.PREFIX + "§c数値エラー");
            }
            return true;
        }

        return true;
    }
}
