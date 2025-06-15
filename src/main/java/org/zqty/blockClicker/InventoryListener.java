// src/main/java/org/zqty/blockClicker/InventoryListener.java
package org.zqty.blockClicker;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryListener implements Listener {
    private final Main plugin;
    private final String mainTitle;
    private final String boostTitle;

    public InventoryListener(Main plugin) {
        this.plugin    = plugin;
        this.mainTitle = ChatColor.translateAlternateColorCodes(
                '&', plugin.getConfig().getString("gui.title"));
        this.boostTitle = ChatColor.translateAlternateColorCodes(
                '&', plugin.getConfig().getString("gui.boost-gui.title", "Boosts"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent ev) {
        if (!(ev.getWhoClicked() instanceof Player)) return;
        InventoryView view = ev.getView();
        String title = view.getTitle();

        // Si on est dans l'un de nos GUI, on annule toujours le déplacement d'item
        if (title.equals(mainTitle) || title.equals(boostTitle)) {
            ev.setCancelled(true);

            Player p = (Player) ev.getWhoClicked();
            int raw = ev.getRawSlot();

            // === Menu principal ===
            if (title.equals(mainTitle)) {
                int slotU = plugin.getConfig().getInt("gui.upgrade-item.slot");
                int slotB = plugin.getConfig().getInt("gui.boost-item.slot");

                if (raw == slotU) {
                    // Achat d’upgrade
                    boolean ok = plugin.getUpgradeManager().purchaseUpgrade(p);
                    if (ok) {
                        int newLvl = plugin.getPlayerDataManager()
                                .getPlayerData(p.getUniqueId())
                                .getUpgradeLevel();
                        Map<String,String> ph = new HashMap<>();
                        ph.put("level", NumberUtil.formatNumber(newLvl));
                        p.sendMessage(plugin.getMessagesManager()
                                .get("upgrade_purchased", ph));
                        p.openInventory(plugin.getGuiManager().getMainGui(p));
                    }
                } else if (raw == slotB) {
                    // Ouvre menu boosts
                    p.openInventory(plugin.getGuiManager().getBoostGui(p));
                }
            }

            // === Menu des boosts ===
            else { // title.equals(boostTitle)
                int size = view.getTopInventory().getSize();

                // click-boosts (slots 10,11,12...)
                List<Map<?, ?>> cbs = plugin.getConfig().getMapList("boosts.click-boosts");
                for (int i = 0; i < cbs.size(); i++) {
                    if (raw == plugin.getConfig()
                            .getInt("gui.boost-gui.slot-click-start", 10) + i) {
                        boolean ok = plugin.getBoostManager().purchaseClickBoost(p, i);
                        if (ok) p.closeInventory();
                        return;
                    }
                }

                // passive-boosts (slots 16,17,18...)
                List<Map<?, ?>> pbs = plugin.getConfig().getMapList("boosts.passive-boosts");
                for (int i = 0; i < pbs.size(); i++) {
                    if (raw == plugin.getConfig()
                            .getInt("gui.boost-gui.slot-passive-start", 16) + i) {
                        boolean ok = plugin.getBoostManager().purchasePassiveBoost(p, i);
                        if (ok) p.closeInventory();
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent ev) {
        InventoryView view = ev.getView();
        String title = view.getTitle();
        if (title.equals(mainTitle) || title.equals(boostTitle)) {
            // Annule tout glisser-déposer dans nos GUI
            ev.setCancelled(true);
        }
    }
}
