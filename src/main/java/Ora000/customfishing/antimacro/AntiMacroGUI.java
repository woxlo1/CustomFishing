package Ora000.customfishing.antimacro;

import Ora000.customfishing.Customfishing;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AntiMacroGUI {

    private static final String TITLE = "§c§lマクロ対策確認";

    private static final Map<UUID, Integer> answer = new HashMap<>();

    public static void open(Customfishing plugin, Player p) {

        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        int correct = new Random().nextInt(9);
        answer.put(p.getUniqueId(), correct);

        for (int i = 0; i < 54; i++) {
            ItemStack it = new ItemStack(
                    i == correct ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE
            );
            ItemMeta m = it.getItemMeta();
            m.setDisplayName(
                    i == correct ? "§aここをクリック！" : "§cハズレ"
            );
            it.setItemMeta(m);
            inv.setItem(i, it);
        }

        p.openInventory(inv);
        p.playSound(p.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
    }

    public static boolean isCorrect(Player p, int slot) {
        return answer.getOrDefault(p.getUniqueId(), -1) == slot;
    }

    public static void clear(Player p) {
        answer.remove(p.getUniqueId());
    }
}
