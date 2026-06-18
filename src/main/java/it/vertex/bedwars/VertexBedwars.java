package it.vertex.bedwars;

import it.vertex.bedwars.commands.*;
import it.vertex.bedwars.listeners.*;
import it.vertex.bedwars.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class VertexBedwars extends JavaPlugin {

    private static VertexBedwars instance;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private StatsManager statsManager;
    private ShopManager shopManager;
    private ScoreboardManager scoreboardManager;
    private TrapManager trapManager;
    private KillStreakManager killStreakManager;
    private LobbySignManager lobbySignManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Init managers
        this.arenaManager      = new ArenaManager(this);
        this.gameManager       = new GameManager(this);
        this.statsManager      = new StatsManager(this);
        this.shopManager       = new ShopManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.trapManager       = new TrapManager(this);
        this.killStreakManager  = new KillStreakManager();
        this.lobbySignManager  = new LobbySignManager(this); // auto-registra eventi

        arenaManager.loadArenas();

        // Register commands
        getCommand("bedwars").setExecutor(new BedwarsCommand(this));
        getCommand("bwjoin").setExecutor(new JoinCommand(this));
        getCommand("bwleave").setExecutor(new LeaveCommand(this));
        getCommand("bwstats").setExecutor(new StatsCommand(this));
        getCommand("bwsetup").setExecutor(new SetupCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new GameListener(this),      this);
        getServer().getPluginManager().registerEvents(new BedListener(this),       this);
        getServer().getPluginManager().registerEvents(new ShopListener(this),      this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this),    this);
        getServer().getPluginManager().registerEvents(new BlockListener(this),     this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(this), this);
        getServer().getPluginManager().registerEvents(new TrapListener(this),      this);

        getLogger().info("§b[VertexBedwars] §aPlugin avviato con successo!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) gameManager.stopAllGames();
        if (statsManager != null) statsManager.saveAll();
        getLogger().info("§b[VertexBedwars] §cPlugin disabilitato.");
    }

    public static VertexBedwars getInstance()          { return instance; }
    public ArenaManager getArenaManager()               { return arenaManager; }
    public GameManager getGameManager()                 { return gameManager; }
    public StatsManager getStatsManager()               { return statsManager; }
    public ShopManager getShopManager()                 { return shopManager; }
    public ScoreboardManager getScoreboardManager()     { return scoreboardManager; }
    public TrapManager getTrapManager()                 { return trapManager; }
    public KillStreakManager getKillStreakManager()     { return killStreakManager; }
    public LobbySignManager getLobbySignManager()       { return lobbySignManager; }
}
