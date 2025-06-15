package org.zqty.blockClicker;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BoostManager {
    private final Main plugin;

    private final Map<UUID, Map<Integer, Long>> clickTimestamps   = new HashMap<>();
    private final Map<UUID, Map<Integer, Long>> passiveTimestamps = new HashMap<>();

    public BoostManager(Main plugin) {
        this.plugin = plugin;
    }

    public boolean purchaseClickBoost(Player p, int index) {
        FileConfiguration cfg = plugin.getConfig();
        List<Map<?, ?>> boosts = cfg.getMapList("boosts.click-boosts");
        if (index < 0 || index >= boosts.size()) return false;

        Map<?, ?> m = boosts.get(index);
        double cost = ((Number) m.get("cost")).doubleValue();
        double extra = ((Number) m.get("extra-points-per-click")).doubleValue();
        int duration = ((Number) m.get("duration")).intValue();
        int cooldown = m.containsKey("cooldown") ? ((Number) m.get("cooldown")).intValue() : 0;

        UUID uuid = p.getUniqueId();
        long now = System.currentTimeMillis();
        long last = clickTimestamps.computeIfAbsent(uuid, k -> new HashMap<>()).getOrDefault(index, 0L);
        long elapsedSec = (now - last) / 1000;
        if (elapsedSec < cooldown) {
            int remaining = (int) (cooldown - elapsedSec);
            Map<String, String> ph = new HashMap<>();
            ph.put("time", String.valueOf(remaining));
            p.sendMessage(plugin.getMessagesManager().get("boost_cooldown", ph));
            return false;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(uuid);
        if (data.getPoints() < cost) {
            String costStr = NumberUtil.formatNumber(cost);
            p.sendMessage(ChatColor.RED + "Points insuffisants (" + costStr + " requis).");
            return false;
        }

        data.addPoints(-cost);
        long end = System.currentTimeMillis() + (duration * 1000L);
        data.setClickBoost(end, extra);
        clickTimestamps.get(uuid).put(index, now);

        String extraStr = NumberUtil.formatNumber(extra);
        p.sendMessage(ChatColor.GREEN + "+ " + extraStr + " p/c pendant " + duration + "s !");
        return true;
    }

    public boolean purchasePassiveBoost(Player p, int index) {
        FileConfiguration cfg = plugin.getConfig();
        List<Map<?, ?>> boosts = cfg.getMapList("boosts.passive-boosts");
        if (index < 0 || index >= boosts.size()) return false;

        Map<?, ?> m = boosts.get(index);
        double cost = ((Number) m.get("cost")).doubleValue();
        double pps = ((Number) m.get("points-per-second")).doubleValue();
        int duration = ((Number) m.get("duration")).intValue();
        int cooldown = m.containsKey("cooldown") ? ((Number) m.get("cooldown")).intValue() : 0;

        UUID uuid = p.getUniqueId();
        long now = System.currentTimeMillis();
        long last = passiveTimestamps.computeIfAbsent(uuid, k -> new HashMap<>()).getOrDefault(index, 0L);
        long elapsedSec = (now - last) / 1000;
        if (elapsedSec < cooldown) {
            int remaining = (int) (cooldown - elapsedSec);
            Map<String, String> ph = new HashMap<>();
            ph.put("time", String.valueOf(remaining));
            p.sendMessage(plugin.getMessagesManager().get("boost_cooldown", ph));
            return false;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(uuid);
        if (data.getPoints() < cost) {
            String costStr = NumberUtil.formatNumber(cost);
            p.sendMessage(ChatColor.RED + "Points insuffisants (" + costStr + " requis).");
            return false;
        }

        data.addPoints(-cost);
        long end = System.currentTimeMillis() + (duration * 1000L);
        data.setPassiveBoost(end, pps);
        passiveTimestamps.get(uuid).put(index, now);

        String ppsStr = NumberUtil.formatNumber(pps);
        p.sendMessage(ChatColor.GREEN + "+ " + ppsStr + " p/s pendant " + duration + "s !");
        return true;
    }
}
