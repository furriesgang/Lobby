package dev.notmycode.labs.lobby;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;

public class TabList {
    private static final String HEADER =
            "\n" + ChatColor.AQUA + ChatColor.BOLD + "DRAFTMC " + ChatColor.DARK_AQUA + ChatColor.BOLD + "NETWORK" +
                    "\n" + ChatColor.GRAY + "play.draftmc.co.site" + "\n" +
                    "\n" + ChatColor.WHITE + "There's currently " + ChatColor.AQUA + ChatColor.UNDERLINE + "%online%" + ChatColor.WHITE + " players online." +
                    "\n";

    private static final String FOOTER =
            "\n" + ChatColor.WHITE + "Ping: " + ChatColor.AQUA + "%ping%" + ChatColor.GRAY + " \uD83D\uDCA0 " + ChatColor.WHITE + "TPS: " + ChatColor.AQUA + "%tps%" +
                    "\n" +
                    "\n" + ChatColor.WHITE + "Join our " + ChatColor.AQUA + ChatColor.UNDERLINE + "Discord Server" + ChatColor.WHITE + " below!" + "\n" +
                    "\n" + ChatColor.GRAY + "discord.draftmc.co.site" + "\n";

    private final Lobby plugin;
    private ScheduledTask task;

    public TabList(Lobby plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, (scheduledTask) -> {
            updateTabList();
        }, 1, 20);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    private void updateTabList() {
        String header = HEADER
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));

        long maxRam = Runtime.getRuntime().maxMemory() / 1048576;
        long usedRam = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
        double tps = Bukkit.getTPS()[0];
        double cpuLoad = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class).getCpuLoad() * 100;

        String tpsColored = (tps > 18 ? ChatColor.AQUA : tps > 16 ? ChatColor.DARK_AQUA : ChatColor.RED) +
                String.format("%.1f", tps);
        String cpuColored = (cpuLoad > 75 ? ChatColor.RED : cpuLoad > 50 ? ChatColor.YELLOW : ChatColor.GREEN) +
                String.format("%.1f%%", cpuLoad);


        for (Player player : Bukkit.getOnlinePlayers()) {
            int ping = player.getPing();
            String pingColored = (ping < 100 ? ChatColor.AQUA : ping < 200 ? ChatColor.DARK_AQUA : ChatColor.RED) +
                    String.valueOf(ping);
            String footer = FOOTER
                    .replace("%used%", String.valueOf(usedRam))
                    .replace("%max%", String.valueOf(maxRam))
                    .replace("%tps%", tpsColored)
                    .replace("%cpu%", cpuColored)
                    .replace("%ping%", pingColored);
            player.setPlayerListHeader(header);
            player.setPlayerListFooter(footer);
        }
    }
}