// src/main/java/org/zqty/blockClicker/BlocksManager.java
package org.zqty.blockClicker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BlocksManager {
    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration cfg;

    public BlocksManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "blocks.yml");
        if (!file.exists()) {
            plugin.saveResource("blocks.yml", false);
        }
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public Map<String, Location> getAll() {
        if (!cfg.isConfigurationSection("blocks")) {
            return Collections.emptyMap();
        }
        Map<String, Location> map = new HashMap<>();
        for (String id : cfg.getConfigurationSection("blocks").getKeys(false)) {
            String p = "blocks." + id;
            String w = cfg.getString(p + ".world");
            int x = cfg.getInt(p + ".x");
            int y = cfg.getInt(p + ".y");
            int z = cfg.getInt(p + ".z");
            World world = Bukkit.getWorld(w);
            if (world != null) {
                map.put(id, new Location(world, x, y, z));
            }
        }
        return map;
    }

    public void add(String id, Location loc) {
        String p = "blocks." + id;
        cfg.set(p + ".world", loc.getWorld().getName());
        cfg.set(p + ".x",     loc.getBlockX());
        cfg.set(p + ".y",     loc.getBlockY());
        cfg.set(p + ".z",     loc.getBlockZ());
        save();
    }

    private void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder blocks.yml: " + e.getMessage());
        }
    }

    public boolean isClickerBlock(Block block) {
        Location bLoc = block.getLocation();
        for (Location loc : getAll().values()) {
            if (loc.getWorld().equals(bLoc.getWorld())
                    && loc.getBlockX() == bLoc.getBlockX()
                    && loc.getBlockY() == bLoc.getBlockY()
                    && loc.getBlockZ() == bLoc.getBlockZ()) {
                return true;
            }
        }
        return false;
    }
}
