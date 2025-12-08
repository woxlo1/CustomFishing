package Ora000.customfishing.antimacro;

import Ora000.customfishing.Customfishing;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AntiMacroManager {

    private final Customfishing plugin;

    private final Map<UUID, Long> lastCheck = new HashMap<>();
    private final Map<UUID, Location> moveStart = new HashMap<>();
    private final Set<UUID> checking = new HashSet<>();

    public AntiMacroManager(Customfishing plugin) {
        this.plugin = plugin;
    }

    /* ==================================================
     * 釣り成功時呼び出し
     * ================================================== */
    public void onFishing(Player p) {

        if (checking.contains(p.getUniqueId())) return;

        long now = System.currentTimeMillis();
        long last = lastCheck.getOrDefault(p.getUniqueId(), 0L);

        if (now - last < randomTime()) return;
        lastCheck.put(p.getUniqueId(), now);

        checking.add(p.getUniqueId());

        // 50% GUI / 50% 移動
        if (Math.random() < 0.5) {
            startGuiCheck(p);
        } else {
            startMoveCheck(p);
        }
    }

    /* ==================================================
     * 移動チェック
     * ================================================== */
    private void startMoveCheck(Player p) {

        moveStart.put(p.getUniqueId(), p.getLocation());

        Bukkit.broadcastMessage(
                Customfishing.PREFIX +
                        "§c【マクロ対策】§e5マス以上移動してください！"
        );

        new BukkitRunnable() {
            int time = 15;

            @Override
            public void run() {

                if (!checking.contains(p.getUniqueId())) {
                    cancel();
                    return;
                }

                if (--time <= 0) {
                    fail(p);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    /* PlayerMoveEvent から呼ばれる */
    public void onMove(Player p) {

        if (!checking.contains(p.getUniqueId())) return;
        if (!moveStart.containsKey(p.getUniqueId())) return;

        Location start = moveStart.get(p.getUniqueId());
        if (start.distance(p.getLocation()) >= 5) {
            success(p);
        }
    }

    /* ==================================================
     * GUIチェック
     * ================================================== */
    private void startGuiCheck(Player p) {

        AntiMacroGUI.open(plugin, p);

        new BukkitRunnable() {
            int time = 10;

            @Override
            public void run() {

                if (!checking.contains(p.getUniqueId())) {
                    cancel();
                    return;
                }

                if (--time <= 0) {
                    fail(p);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    /* GUIクリック成功時に呼ぶ */
    public void guiSuccess(Player p) {
        if (!checking.contains(p.getUniqueId())) return;
        success(p);
    }

    /* ==================================================
     * 成功 / 失敗
     * ================================================== */
    private void success(Player p) {
        checking.remove(p.getUniqueId());
        moveStart.remove(p.getUniqueId());

        p.closeInventory();
        p.sendMessage(Customfishing.PREFIX + "§a✅ 操作確認完了");
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
    }

    public void fail(Player p) {

        checking.remove(p.getUniqueId());
        moveStart.remove(p.getUniqueId());

        p.closeInventory();
        p.sendMessage(Customfishing.PREFIX +
                "§c❌ マクロ疑い：操作確認失敗");

        // ✅ OP / 管理者に通知（クリックでTP）
        TextComponent msg = new TextComponent(Customfishing.PREFIX);
        TextComponent warn = new TextComponent("§c【警告】 ");
        TextComponent name = new TextComponent("§e" + p.getName());
        TextComponent tail = new TextComponent(" §7がマクロ対策に失敗しました");

        name.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/minecraft:tp " + p.getName()
        ));

        name.setHoverEvent(
                new net.md_5.bungee.api.chat.HoverEvent(
                        net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("§aクリックしてTP").create()
                )
        );

        Bukkit.getOnlinePlayers().forEach(pl -> {
            if (pl.isOp() || pl.hasPermission("customfishing.admin")) {
                pl.spigot().sendMessage(msg, warn, name, tail);
            }
        });

        Bukkit.getOnlinePlayers().forEach(pl -> {
            if (pl.isOp() || pl.hasPermission("customfishing.admin")) {
                pl.sendMessage(msg);
            }
        });

        p.playSound(p.getLocation(),
                Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);

        // ✅ ここに追記可能
        // SellLogger.logMacroFail(p);
        // disableFishing(p);
    }

    private long randomTime() {
        return (2 + new Random().nextInt(4)) * 60L * 1000L;
    }
}
