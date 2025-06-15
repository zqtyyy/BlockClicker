package org.zqty.blockClicker;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {
    private static Main instance;

    private ConfigManager       configManager;
    private MessagesManager     messagesManager;
    private PlayerDataManager   playerDataManager;
    private GUIManager          guiManager;
    private UpgradeManager      upgradeManager;
    private BoostManager        boostManager;
    private BlocksManager       blocksManager;
    private final Map<UUID,String> pendingAdd = new HashMap<>();

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        messagesManager   = new MessagesManager(this);
        messagesManager.load();

        configManager     = new ConfigManager(this);
        configManager.load();

        playerDataManager = new PlayerDataManager(this);
        playerDataManager.loadAll();

        guiManager        = new GUIManager(this);
        upgradeManager    = new UpgradeManager(this);
        boostManager      = new BoostManager(this);
        blocksManager     = new BlocksManager(this);

        getServer().getPluginManager().registerEvents(new ClickListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        getCommand("bc").setExecutor(new BCCommand(this, pendingAdd, blocksManager));

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new BCPlaceholderExpansion(this).register();
        }

        // Sauvegarde périodique
        int interval = getConfig().getInt("save-interval", 5);
        new BukkitRunnable() {
            @Override
            public void run() {
                playerDataManager.saveAll();
            }
        }.runTaskTimer(this, interval * 60 * 20L, interval * 60 * 20L);

        // Boost passif : points automatiques
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Map.Entry<UUID, PlayerData> entry : playerDataManager.getAllPlayerData().entrySet()) {
                    PlayerData data = entry.getValue();
                    if (now < data.getPassiveBoostEnd()) {
                        data.addPoints(data.getPassiveBoostRate());
                    }
                }
            }
        }.runTaskTimer(this, 20L, 20L); // Toutes les secondes
    }

    @Override
    public void onDisable() {
        playerDataManager.saveAll();
    }

    public ConfigManager       getConfigManager()   { return configManager; }
    public MessagesManager     getMessagesManager() { return messagesManager; }
    public PlayerDataManager   getPlayerDataManager(){ return playerDataManager; }
    public GUIManager          getGuiManager()      { return guiManager; }
    public UpgradeManager      getUpgradeManager()  { return upgradeManager; }
    public BoostManager        getBoostManager()    { return boostManager; }
    public BlocksManager       getBlocksManager()   { return blocksManager; }
    public Map<UUID,String>    getPendingAddMap()   { return pendingAdd; }
}
