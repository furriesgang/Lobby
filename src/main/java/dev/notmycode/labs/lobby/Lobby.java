package dev.notmycode.labs.lobby;

import org.bukkit.plugin.java.JavaPlugin;

public final class Lobby extends JavaPlugin {
    private TabList tabList;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new Listeners(this), this);

        Commands commands = new Commands(this);
        getCommand("tpsbar").setExecutor(commands);
        getCommand("rambar").setExecutor(commands);
        getCommand("cpubar").setExecutor(commands);
        this.getCommand("spawn").setExecutor(new SpawnCommand());
        tabList = new TabList(this);
    }

    @Override
    public void onDisable() {
        if (tabList != null) {
            tabList.stop();
        }
    }
}
