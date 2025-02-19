package dev.notmycode.labs.lobby;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

public class Utils {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static BossBar createRamBar() {
        long max = Runtime.getRuntime().maxMemory() / 1048576;
        long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
        double percentage = (double) used / max;

        ChatColor color = percentage > 0.75 ? ChatColor.RED :
                percentage > 0.5 ? ChatColor.YELLOW :
                        ChatColor.GREEN;

        String title = color + "RAM: " + df.format(percentage * 100) + "% " +
                "(" + ChatColor.GREEN + used + "MB" + ChatColor.WHITE + "/" +
                ChatColor.RED + max + "MB" + ChatColor.WHITE + ")";

        BossBar bar = Bukkit.createBossBar(title,
                percentage > 0.75 ? BarColor.RED :
                        percentage > 0.5 ? BarColor.YELLOW :
                                BarColor.GREEN,
                BarStyle.SOLID);
        bar.setProgress(Math.min(Math.max(percentage, 0.0), 1.0));
        return bar;
    }

    public static BossBar createTpsBar(double tps) {
        double progress = Math.min(tps / 20.0, 1.0);

        ChatColor color = tps > 18 ? ChatColor.GREEN :
                tps > 16 ? ChatColor.YELLOW :
                        ChatColor.RED;

        String title = color + "TPS: " + df.format(tps);

        BossBar bar = Bukkit.createBossBar(title,
                tps > 18 ? BarColor.GREEN :
                        tps > 16 ? BarColor.YELLOW :
                                BarColor.RED,
                BarStyle.SOLID);
            bar.setProgress(progress);
            return bar;
        }

    public static BossBar createCpuBar() {
        double cpuLoad = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class).getCpuLoad() * 100;
        double progress = Math.min(cpuLoad / 100.0, 1.0);

        ChatColor color = cpuLoad > 75 ? ChatColor.RED :
                cpuLoad > 50 ? ChatColor.YELLOW :
                        ChatColor.GREEN;

        String title = color + "CPU: " + df.format(cpuLoad) + "%";

        BossBar bar = Bukkit.createBossBar(title,
                cpuLoad > 75 ? BarColor.RED :
                        cpuLoad > 50 ? BarColor.YELLOW :
                                BarColor.GREEN,
                BarStyle.SOLID);
        bar.setProgress(progress);
        return bar;
    }
    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}