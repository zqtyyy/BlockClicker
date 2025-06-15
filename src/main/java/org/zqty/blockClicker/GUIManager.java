// src/main/java/org/zqty/blockClicker/GUIManager.java
package org.zqty.blockClicker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUIManager {
    private final Main plugin;
    private final ConfigManager cfg;

    public GUIManager(Main plugin) {
        this.plugin = plugin;
        this.cfg    = plugin.getConfigManager();
    }

    /**
     * Menu principal : Upgrades et Boosts résumé
     */
    public Inventory getMainGui(Player p) {
        FileConfiguration config = cfg.getConfig();
        int rows = config.getInt("gui.rows", 3);
        String title = ChatColor.translateAlternateColorCodes(
                '&', config.getString("gui.title", "Boutique"));
        Inventory inv = Bukkit.createInventory(null, rows * 9, title);

        // filler
        ItemStack filler = cfg.getGuiItem("filler-item");
        if (filler.getType() != Material.AIR) {
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, filler);
            }
        }

        // Upgrade slot
        ItemStack up = cfg.getGuiItem("upgrade-item");
        int slotU = config.getInt("gui.upgrade-item.slot", 11);
        int currentLevel = plugin.getPlayerDataManager()
                .getPlayerData(p.getUniqueId())
                .getUpgradeLevel();
        int nextCost = UpgradeManager.calculateCost(config, currentLevel + 1);
        ItemMeta um = up.getItemMeta();
        if (um != null && um.hasLore()) {
            List<String> lore = um.getLore().stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line)
                            .replace("%level%", String.valueOf(currentLevel))
                            .replace("%cost%",  NumberUtil.formatNumber(nextCost)))
                    .collect(Collectors.toList());
            um.setLore(lore);
            up.setItemMeta(um);
        }
        inv.setItem(slotU, up);

        // Boost slot
        ItemStack bt = cfg.getGuiItem("boost-item");
        int slotB = config.getInt("gui.boost-item.slot", 15);
        // find minimal cost among all boosts
        double minCost = Double.MAX_VALUE;
        for (Map<?,?> m : config.getMapList("boosts.click-boosts")) {
            minCost = Math.min(minCost, ((Number)m.get("cost")).doubleValue());
        }
        for (Map<?,?> m : config.getMapList("boosts.passive-boosts")) {
            minCost = Math.min(minCost, ((Number)m.get("cost")).doubleValue());
        }
        if (minCost==Double.MAX_VALUE) minCost = 0;
        ItemMeta bm = bt.getItemMeta();
        if (bm!=null && bm.hasLore()) {
            String costStr = NumberUtil.formatNumber(minCost);
            List<String> lore = bm.getLore().stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line)
                            .replace("%cost%", costStr))
                    .collect(Collectors.toList());
            bm.setLore(lore);
            bt.setItemMeta(bm);
        }
        inv.setItem(slotB, bt);

        return inv;
    }

    /**
     * Menu détaillé des boosts, avec lore et titre configurables
     */
    public Inventory getBoostGui(Player p) {
        FileConfiguration config = cfg.getConfig();
        int rows = config.getInt("gui.rows", 3);
        String title = ChatColor.translateAlternateColorCodes(
                '&', config.getString("gui.boost-gui.title", "Boosts"));
        Inventory inv = Bukkit.createInventory(null, rows * 9, title);

        // filler
        ItemStack filler = cfg.getGuiItem("filler-item");
        if (filler.getType() != Material.AIR) {
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, filler);
            }
        }

        // templates lore
        List<String> clickLoreTpl = config.getStringList("gui.boost-gui.lore.click");
        List<String> passiveLoreTpl = config.getStringList("gui.boost-gui.lore.passive");

        int clickStart = config.getInt("gui.boost-gui.slot-click-start", 10);
        int passiveStart = config.getInt("gui.boost-gui.slot-passive-start", 16);

        // click-boosts
        List<Map<?, ?>> cbs = config.getMapList("boosts.click-boosts");
        for (int i = 0; i < cbs.size(); i++) {
            Map<?, ?> m = cbs.get(i);
            ItemStack item = new ItemStack(Material.valueOf((String)m.get("material")));
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', (String)m.get("name")));
                List<String> lore = clickLoreTpl.stream()
                        .map(line -> ChatColor.translateAlternateColorCodes('&', line)
                                .replace("%cost%",    NumberUtil.formatNumber(((Number)m.get("cost")).doubleValue()))
                                .replace("%extra_pcc%", NumberUtil.formatNumber(((Number)m.get("extra-points-per-click")).doubleValue()))
                                .replace("%duration%", String.valueOf(((Number)m.get("duration")).intValue()))
                                .replace("%cooldown%", String.valueOf(((Number)m.get("cooldown")).intValue()))
                        )
                        .collect(Collectors.toList());
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(clickStart + i, item);
        }

        // passive-boosts
        List<Map<?, ?>> pbs = config.getMapList("boosts.passive-boosts");
        for (int i = 0; i < pbs.size(); i++) {
            Map<?, ?> m = pbs.get(i);
            ItemStack item = new ItemStack(Material.valueOf((String)m.get("material")));
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', (String)m.get("name")));
                List<String> lore = passiveLoreTpl.stream()
                        .map(line -> ChatColor.translateAlternateColorCodes('&', line)
                                .replace("%cost%",    NumberUtil.formatNumber(((Number)m.get("cost")).doubleValue()))
                                .replace("%pps%",     NumberUtil.formatNumber(((Number)m.get("points-per-second")).doubleValue()))
                                .replace("%duration%", String.valueOf(((Number)m.get("duration")).intValue()))
                                .replace("%cooldown%", String.valueOf(((Number)m.get("cooldown")).intValue()))
                        )
                        .collect(Collectors.toList());
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(passiveStart + i, item);
        }

        return inv;
    }
}
