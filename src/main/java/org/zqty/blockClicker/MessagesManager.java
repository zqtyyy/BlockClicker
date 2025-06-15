package org.zqty.blockClicker;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

public class MessagesManager {
    private final JavaPlugin plugin;
    private FileConfiguration cfg;

    public MessagesManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public void load() {
        // Copie messages.yml par défaut si absent
        plugin.saveResource("messages.yml", false);
        // Charge le fichier
        File file = new File(plugin.getDataFolder(), "messages.yml");
        cfg = YamlConfiguration.loadConfiguration(file);
    }


    public String get(String key) {
        String raw = cfg.getString("messages." + key, key);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }


    public String get(String key, Map<String, String> placeholders) {
        String msg = get(key);
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            msg = msg.replace("%" + e.getKey() + "%", e.getValue());
        }
        return msg;
    }
}
