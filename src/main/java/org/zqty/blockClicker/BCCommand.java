// src/main/java/org/zqty/blockClicker/BCCommand.java
package org.zqty.blockClicker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class BCCommand implements CommandExecutor {
    private final Main plugin;
    private final Map<UUID,String> pendingAdd;
    private final BlocksManager blocksManager;

    public BCCommand(Main plugin, Map<UUID,String> pendingAdd, BlocksManager blocksManager) {
        this.plugin = plugin;
        this.pendingAdd = pendingAdd;
        this.blocksManager = blocksManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MessagesManager msg = plugin.getMessagesManager();
        if (args.length == 0) {
            Map<String,String> ph = new HashMap<>();
            ph.put("usage", "/" + label + " <add|set|resettop|reload|points|give|top>");
            sender.sendMessage(msg.get("usage", ph));
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "add":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msg.get("no_permission"));
                    return true;
                }
                if (args.length != 2) {
                    Map<String,String> ph = new HashMap<>();
                    ph.put("usage", "/" + label + " add <id>");
                    sender.sendMessage(msg.get("usage", ph));
                    return true;
                }
                Player p = (Player) sender;
                pendingAdd.put(p.getUniqueId(), args[1]);
                Map<String,String> ph1 = new HashMap<>();
                ph1.put("id", args[1]);
                p.sendMessage(msg.get("add_instruction", ph1));
                return true;

            case "set":
                if (!sender.hasPermission("blockclicker.commands.set")) {
                    sender.sendMessage(msg.get("no_permission"));
                    return true;
                }
                if (args.length != 3) {
                    Map<String,String> ph = new HashMap<>();
                    ph.put("usage", "/" + label + " set <player> <amount>");
                    sender.sendMessage(msg.get("usage", ph));
                    return true;
                }
                Player targetSet = Bukkit.getPlayer(args[1]);
                if (targetSet == null) {
                    sender.sendMessage(msg.get("player_not_found"));
                    return true;
                }
                try {
                    int amt = Integer.parseInt(args[2]);
                    plugin.getPlayerDataManager()
                            .getPlayerData(targetSet.getUniqueId())
                            .setPoints(amt);
                    Map<String,String> ph2 = new HashMap<>();
                    ph2.put("player", targetSet.getName());
                    ph2.put("amount", NumberUtil.formatNumber(amt));
                    sender.sendMessage(msg.get("set_success", ph2));
                    targetSet.sendMessage(msg.get("set_notify", ph2));
                } catch (NumberFormatException e) {
                    sender.sendMessage(msg.get("invalid_number"));
                }
                return true;

            case "resettop":
                if (!sender.hasPermission("blockclicker.commands.resettop")) {
                    sender.sendMessage(msg.get("no_permission"));
                    return true;
                }
                // reset all
                for (PlayerData pd : plugin.getPlayerDataManager().getAllPlayerData().values()) {
                    pd.setPoints(0.0);
                }
                sender.sendMessage(msg.get("resettop_success"));
                return true;

            case "reload":
                if (!sender.hasPermission("blockclicker.commands.reload")) {
                    sender.sendMessage(msg.get("no_permission"));
                    return true;
                }
                plugin.reloadConfig();
                plugin.getConfigManager().load();
                sender.sendMessage(msg.get("reload_success"));
                return true;

            case "points":
                if (!sender.hasPermission("blockclicker.commands.points")) {
                    sender.sendMessage(msg.get("no_permission"));
                    return true;
                }
                if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        Map<String,String> ph = new HashMap<>();
                        ph.put("usage", "/" + label + " points <player>");
                        sender.sendMessage(msg.get("usage", ph));
                        return true;
                    }
                    Player pp = (Player) sender;
                    double pts = plugin.getPlayerDataManager().getPoints(pp.getUniqueId());
                    Map<String,String> ph3 = new HashMap<>();
                    ph3.put("points", NumberUtil.formatNumber(pts));
                    pp.sendMessage(msg.get("player_points", ph3));
                } else {
                    Player tgt = Bukkit.getPlayer(args[1]);
                    if (tgt == null) {
                        sender.sendMessage(msg.get("player_not_found"));
                    } else {
                        double pts = plugin.getPlayerDataManager().getPoints(tgt.getUniqueId());
                        Map<String,String> ph4 = new HashMap<>();
                        ph4.put("player", tgt.getName());
                        ph4.put("points", NumberUtil.formatNumber(pts));
                        sender.sendMessage(msg.get("player_points_other", ph4));
                    }
                }
                return true;

            case "give":
                if (!sender.hasPermission("blockclicker.commands.give")) {
                    sender.sendMessage(msg.get("no_permission"));
                    return true;
                }
                if (args.length != 3) {
                    Map<String,String> ph = new HashMap<>();
                    ph.put("usage", "/" + label + " give <player> <amount>");
                    sender.sendMessage(msg.get("usage", ph));
                    return true;
                }
                Player tgtGive = Bukkit.getPlayer(args[1]);
                if (tgtGive == null) {
                    sender.sendMessage(msg.get("player_not_found"));
                    return true;
                }
                try {
                    int amt = Integer.parseInt(args[2]);
                    plugin.getPlayerDataManager()
                            .getPlayerData(tgtGive.getUniqueId())
                            .addPoints(amt);
                    Map<String,String> phS = new HashMap<>();
                    phS.put("player", tgtGive.getName());
                    phS.put("amount", NumberUtil.formatNumber(amt));
                    sender.sendMessage(msg.get("given_points", phS));
                    Map<String,String> phT = new HashMap<>();
                    phT.put("amount", NumberUtil.formatNumber(amt));
                    tgtGive.sendMessage(msg.get("received_points", phT));
                } catch (NumberFormatException e) {
                    sender.sendMessage(msg.get("invalid_number"));
                }
                return true;

            case "top":
                if (!sender.hasPermission("blockclicker.commands.top")) {
                    sender.sendMessage(msg.get("no_permission"));
                    return true;
                }
                int page = 1;
                if (args.length >= 2) {
                    try { page = Integer.parseInt(args[1]); }
                    catch (NumberFormatException e) {
                        sender.sendMessage(msg.get("invalid_number"));
                        return true;
                    }
                }
                // display top 10 per page
                List<Entry<UUID,PlayerData>> sorted = new ArrayList<>(plugin.getPlayerDataManager().getAllPlayerData().entrySet());
                Collections.sort(sorted, new Comparator<Entry<UUID,PlayerData>>() {
                    @Override public int compare(Entry<UUID,PlayerData> e1, Entry<UUID,PlayerData> e2) {
                        return Double.compare(e2.getValue().getPoints(), e1.getValue().getPoints());
                    }
                });
                int perPage = 10;
                int maxPage = (sorted.size() + perPage - 1) / perPage;
                if (page < 1 || page > maxPage) {
                    sender.sendMessage(msg.get("invalid_number"));
                    return true;
                }
                Map<String,String> phH = new HashMap<>();
                phH.put("page", String.valueOf(page));
                phH.put("maxPage", String.valueOf(maxPage));
                sender.sendMessage(msg.get("top_header", phH));

                int start = (page - 1) * perPage;
                int end = Math.min(start + perPage, sorted.size());
                for (int i = start; i < end; i++) {
                    Entry<UUID,PlayerData> ent = sorted.get(i);
                    String name = Bukkit.getOfflinePlayer(ent.getKey()).getName();
                    String pts = NumberUtil.formatNumber(ent.getValue().getPoints());
                    Map<String,String> phL = new HashMap<>();
                    phL.put("position", String.valueOf(i+1));
                    phL.put("player", name);
                    phL.put("points", pts);
                    sender.sendMessage(msg.get("top_entry", phL));
                }
                return true;

            default:
                Map<String,String> ph = new HashMap<>();
                ph.put("usage", "/" + label + " <add|set|resettop|reload|points|give|top>");
                sender.sendMessage(msg.get("usage", ph));
                return true;
        }
    }
}
