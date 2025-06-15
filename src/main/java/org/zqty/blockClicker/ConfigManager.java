// src/main/java/org/zqty/blockClicker/ConfigManager.java
package org.zqty.blockClicker;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConfigManager {
    private final Main plugin;
    private FileConfiguration cfg;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }
    public void load() {
        cfg = plugin.getConfig();
        cfg.options().copyDefaults(true);
        plugin.saveConfig();
    }
    public FileConfiguration getConfig() {
        return cfg;
    }

    public ItemStack getGuiItem(String path) {
        String mat = cfg.getString("gui." + path + ".material", "STONE");
        String name = cfg.getString("gui." + path + ".name", "");
        List<String> lore = cfg.getStringList("gui." + path + ".lore");
        ItemStack item = new ItemStack(Material.valueOf(mat));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (!name.isEmpty()) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }
            if (!lore.isEmpty()) {
                meta.setLore(lore.stream()
                        .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                        .collect(Collectors.toList()));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public String getActionBarMessage(UUID uuid) {
        double pts = plugin.getPlayerDataManager().getPoints(uuid);
        double ppc = plugin.getPlayerDataManager().getPointsPerClick(uuid);
        String sPts = NumberUtil.formatNumber(pts);
        String sPpc = NumberUtil.formatDecimal(ppc, 1);
        String raw = cfg.getString("display.action-bar-message",
                "&ePoints: %points% &7| PPC: %points-per-click%");
        return ChatColor.translateAlternateColorCodes('&',
                raw.replace("%points%", sPts)
                        .replace("%points-per-click%", sPpc)
        );
    }
}
