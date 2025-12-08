package Ora000.customfishing.antimacro;

import Ora000.customfishing.Customfishing;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AntiMacroListener implements Listener {

    private final AntiMacroManager manager;

    public AntiMacroListener(Customfishing plugin) {
        this.manager = plugin.getAntiMacroManager();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!e.getView().getTitle().equals("§c§lマクロ対策確認")) return;

        e.setCancelled(true);

        if (AntiMacroGUI.isCorrect(p, e.getSlot())) {
            p.closeInventory();
            p.sendMessage(Customfishing.PREFIX + "§a✔ 認証完了");
            AntiMacroGUI.clear(p);
        } else {
            p.closeInventory();
            AntiMacroGUI.clear(p);
            manager.fail(p);
        }
    }
}
