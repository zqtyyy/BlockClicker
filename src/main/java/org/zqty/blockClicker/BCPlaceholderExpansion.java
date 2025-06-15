// src/main/java/org/zqty/blockClicker/BCPlaceholderExpansion.java
package org.zqty.blockClicker;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.Map.Entry;

public class BCPlaceholderExpansion extends PlaceholderExpansion {
    private final Main plugin;

    public BCPlaceholderExpansion(Main plugin) {
        this.plugin = plugin;
    }
    @Override public String getIdentifier() { return "bc"; }
    @Override public String getAuthor()     { return "zqty"; }
    @Override public String getVersion()    { return plugin.getDescription().getVersion(); }
    @Override public boolean persist()      { return true; }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params == null || !params.startsWith("top_")) {
            return null;
        }
        // top_<n>_name ou top_<n>_points
        String[] parts = params.split("_");
        if (parts.length != 3) {
            return null;
        }
        int idx;
        try {
            idx = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
        String field = parts[2];

        // classement trié
        List<Entry<UUID,PlayerData>> list = new ArrayList<>(plugin.getPlayerDataManager().getAllPlayerData().entrySet());
        Collections.sort(list, new Comparator<Entry<UUID,PlayerData>>() {
            @Override public int compare(Entry<UUID,PlayerData> a, Entry<UUID,PlayerData> b) {
                return Double.compare(b.getValue().getPoints(), a.getValue().getPoints());
            }
        });
        if (idx < 1 || idx > list.size()) {
            return "";
        }
        Entry<UUID,PlayerData> entry = list.get(idx - 1);
        if ("name".equalsIgnoreCase(field)) {
            return plugin.getServer().getOfflinePlayer(entry.getKey()).getName();
        } else if ("points".equalsIgnoreCase(field)) {
            return NumberUtil.formatNumber(entry.getValue().getPoints());
        }
        return null;
    }
}
