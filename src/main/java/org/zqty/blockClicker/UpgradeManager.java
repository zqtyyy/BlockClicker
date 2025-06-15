// src/main/java/org/zqty/blockClicker/UpgradeManager.java
package org.zqty.blockClicker;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class UpgradeManager {
    private final Main plugin;

    public UpgradeManager(Main plugin) {
        this.plugin = plugin;
    }


    public boolean purchaseUpgrade(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        int current = data.getUpgradeLevel();


        int cost = calculateCost(plugin.getConfig(), current + 1);
        if (data.getPoints() < cost) {

            String costStr = NumberUtil.formatNumber(cost);
            player.sendMessage(ChatColor.RED + "Points insuffisants (" + costStr + " requis).");
            return false;
        }


        data.addPoints(-cost);
        data.incrementUpgradeLevel();
        return true;
    }

    public static int calculateCost(FileConfiguration cfg, int level) {
        double base = cfg.getDouble("upgrades.base-cost", 50.0);
        double mult = cfg.getDouble("upgrades.cost-multiplier", 1.5);
        double raw  = base * Math.pow(mult, level - 1);
        return (int) Math.round(raw);
    }
}
