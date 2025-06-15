// src/main/java/org/zqty/blockClicker/PlayerDataManager.java
package org.zqty.blockClicker;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final Main plugin;
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final File dataFile;
    private final YamlConfiguration dataConfig;

    public PlayerDataManager(Main plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(),
                plugin.getConfig().getString("storage.file", "data.yml"));
        if (!dataFile.exists()) {
            plugin.saveResource(plugin.getConfig().getString("storage.file", "data.yml"), false);
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void loadAll() {
        if (dataConfig.isConfigurationSection("players")) {
            for (String key : dataConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                double pts = dataConfig.getDouble("players." + key + ".points", 0.0);
                int lvl    = dataConfig.getInt("players." + key + ".level", 0);
                PlayerData pd = new PlayerData(pts, lvl);
                playerDataMap.put(uuid, pd);
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()) {
            String path = "players." + entry.getKey().toString();
            dataConfig.set(path + ".points", entry.getValue().getPoints());
            dataConfig.set(path + ".level",  entry.getValue().getUpgradeLevel());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder data.yml: " + e.getMessage());
        }
    }

    /** Accès à la donnée d’un joueur (crée si absent). */
    public PlayerData getPlayerData(UUID uuid) {
        if (!playerDataMap.containsKey(uuid)) {
            playerDataMap.put(uuid, new PlayerData(0.0, 0));
        }
        return playerDataMap.get(uuid);
    }

    /** Pour la commande /bc top et /bc resettop */
    public Map<UUID, PlayerData> getAllPlayerData() {
        return playerDataMap;
    }

    /** Nombre de points total d’un joueur. */
    public double getPoints(UUID uuid) {
        return getPlayerData(uuid).getPoints();
    }

    /** Points gagné par clic d’un joueur. */
    public double getPointsPerClick(UUID uuid) {
        return getPlayerData(uuid).getPointsPerClick();
    }
}
