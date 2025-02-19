package dev.notmycode.labs.lobby;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class Commands implements CommandExecutor {
    private final Lobby plugin;
    private final HashMap<UUID, BarInfo> tpsBars = new HashMap<>();
    private final HashMap<UUID, BarInfo> ramBars = new HashMap<>();
    private final HashMap<UUID, BarInfo> cpuBars = new HashMap<>();

    private static class BarInfo {
        BossBar bar;
        ScheduledTask task;

        BarInfo(BossBar bar, ScheduledTask task) {
            this.bar = bar;
            this.task = task;
        }
    }

    public Commands(Lobby plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        switch (label.toLowerCase()) {
            case "tpsbar" -> toggleTpsBar(player);
            case "rambar" -> toggleRamBar(player);
            case "cpubar" -> toggleCpuBar(player);
        }
        return true;
    }

    private void toggleTpsBar(Player player) {
        UUID uuid = player.getUniqueId();
        if (tpsBars.containsKey(uuid)) {
            tpsBars.get(uuid).task.cancel();
            tpsBars.get(uuid).bar.removePlayer(player);
            tpsBars.remove(uuid);
            return;
        }

        BossBar bar = Utils.createTpsBar(20.0);
        bar.addPlayer(player);

        ScheduledTask task = player.getScheduler().runAtFixedRate(plugin, (scheduledTask) -> {
            double tps = plugin.getServer().getTPS()[0];
            BossBar newBar = Utils.createTpsBar(tps);
            bar.setProgress(newBar.getProgress());
            bar.setTitle(newBar.getTitle());
        }, null, 1, 20);

        tpsBars.put(uuid, new BarInfo(bar, task));
    }

    private void toggleRamBar(Player player) {
        UUID uuid = player.getUniqueId();
        if (ramBars.containsKey(uuid)) {
            ramBars.get(uuid).task.cancel();
            ramBars.get(uuid).bar.removePlayer(player);
            ramBars.remove(uuid);
            return;
        }

        BossBar bar = Utils.createRamBar();
        bar.addPlayer(player);

        ScheduledTask task = player.getScheduler().runAtFixedRate(plugin, (scheduledTask) -> {
            BossBar newBar = Utils.createRamBar();
            bar.setProgress(newBar.getProgress());
            bar.setTitle(newBar.getTitle());
        }, null, 1, 20);

        ramBars.put(uuid, new BarInfo(bar, task));
    }

    private void toggleCpuBar(Player player) {
        UUID uuid = player.getUniqueId();
        if (cpuBars.containsKey(uuid)) {
            cpuBars.get(uuid).task.cancel();
            cpuBars.get(uuid).bar.removePlayer(player);
            cpuBars.remove(uuid);
            return;
        }

        BossBar bar = Utils.createCpuBar();
        bar.addPlayer(player);

        ScheduledTask task = player.getScheduler().runAtFixedRate(plugin, (scheduledTask) -> {
            BossBar newBar = Utils.createCpuBar();
            bar.setProgress(newBar.getProgress());
            bar.setTitle(newBar.getTitle());
        }, null, 1, 20);

        cpuBars.put(uuid, new BarInfo(bar, task));
    }
}