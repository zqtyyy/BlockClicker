// src/main/java/org/zqty/blockClicker/ClickListener.java
package org.zqty.blockClicker;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Block;

public class ClickListener implements Listener {
    private final Main plugin;

    public ClickListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent ev) {
        Player p = ev.getPlayer();
        Block b = ev.getClickedBlock();
        if (b == null) return;

        // Enregistrement de block après /bc add
        if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
            String pending = plugin.getPendingAddMap().remove(p.getUniqueId());
            if (pending != null) {
                plugin.getBlocksManager().add(pending, b.getLocation());
                ev.setCancelled(true);
                return;
            }
        }

        // Si ce n'est pas un block-clicker on ignore
        if (! plugin.getBlocksManager().isClickerBlock(b)) return;

        ev.setCancelled(true);
        if (ev.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Ajout des points
            int gain = (int) plugin.getPlayerDataManager().getPointsPerClick(p.getUniqueId());
            plugin.getPlayerDataManager().getPlayerData(p.getUniqueId()).addPoints(gain);

            // Action-bar
            if (plugin.getConfigManager().getConfig().getBoolean("display.action-bar-enabled", true)) {
                String msg = plugin.getConfigManager().getActionBarMessage(p.getUniqueId());
                ActionBarUtil.sendActionBar(p, msg);
            }
        } else if (ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
            p.openInventory(plugin.getGuiManager().getMainGui(p));
        }
    }
}
