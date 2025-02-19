package dev.notmycode.labs.lobby;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.HashMap;

import static dev.notmycode.labs.lobby.Utils.format;

public class Listeners implements Listener {
    private final Lobby plugin;
    private final HashMap<Player, Boolean> doubleJumped = new HashMap<>();

    public Listeners(Lobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String p = event.getPlayer().getName();
        Component JoinMSG = MiniMessage.miniMessage().deserialize("<aqua>" + p + " <dark_aqua>has joined the lobby!");
        Bukkit.broadcast(JoinMSG);
        Player player = event.getPlayer();
        player.setAllowFlight(true);
        doubleJumped.put(player, false);
        Location loc = player.getLocation();
        final int[] counter = {0};
        Component title = MiniMessage.miniMessage().deserialize("<green>ยินดีต้อนรับสู่ <aqua><b>DraftMC</b><green>!");
        Component title2 = MiniMessage.miniMessage().deserialize("<gold>เลือกเกมส์โหมดที่คุณต้องการเล่น");
        player.showTitle(net.kyori.adventure.title.Title.title(
                title,
                title2
        ));
        if (player.isOp()) {
            player.sendMessage(format("&8&m                                                  "));
            player.sendMessage(format("&b* &fThere is a new version of &bLobby &favailable!"));
            player.sendMessage(format("&b* &fCurrent version: &b" + plugin.getDescription().getVersion()));
            player.sendMessage(format("&b* &fNew version: &b7.2.8-STABLE"));
            player.sendMessage(format("&r"));
            player.sendMessage(format("&b* &fPlease update to the latest version."));
            player.sendMessage(format("&b* &b&nhttp://100.64.193.37/lobby-1.0-SNAPSHOT.jar"));
            player.sendMessage(format("&8&m                                                  "));
        }
        Bukkit.getWorld("world").spawnEntity(loc, EntityType.FIREWORK);
        plugin.getServer().getRegionScheduler().runAtFixedRate(plugin, loc, (task) -> {
            if (counter[0] >= 20) {
                task.cancel();
                return;
            }

            Color[] colors = {
                    Color.AQUA,
                    Color.WHITE,
                    Color.PURPLE
            };

            double radius = counter[0] * 0.2;
            int particles = 20;
            double angle = 2 * Math.PI / particles;

            for (int i = 0; i < particles; i++) {
                double x = radius * Math.cos(angle * i);
                double z = radius * Math.sin(angle * i);
                Location particleLoc = loc.clone().add(x, counter[0] * 0.1, z);

                Color color = colors[(int) ((System.currentTimeMillis() / 250) % colors.length)];
                Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
                player.getWorld().spawnParticle(
                        Particle.REDSTONE,
                        particleLoc,
                        1,
                        0, 0, 0,
                        dustOptions
                );
            }

            counter[0]++;
        }, 1L, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String p = event.getPlayer().getName();
        Component QuitMSG = MiniMessage.miniMessage().deserialize("<dark_aqua>" + p + " <aqua>has left the lobby!");
        Bukkit.broadcast(QuitMSG);
        doubleJumped.remove(event.getPlayer());
    }
    @EventHandler
    public void onPlayerPVP(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getEntity().sendMessage(ChatColor.DARK_AQUA + "You are not allowed to do this.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "You are not allowed to do this.");
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "You are not allowed to do this.");
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);

            if (!doubleJumped.get(player)) {
                player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(1));
                player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.0f);

                plugin.getServer().getRegionScheduler().runAtFixedRate(plugin, player.getLocation(), (task) -> {
                    if (!player.isOnGround()) {
                        Location loc = player.getLocation();

                        Color[] colors = {
                                Color.RED,
                                Color.ORANGE,
                                Color.YELLOW,
                                Color.LIME,
                                Color.AQUA,
                                Color.PURPLE
                        };

                        Color color = colors[(int) ((System.currentTimeMillis() / 250) % colors.length)];
                        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);

                        player.getWorld().spawnParticle(
                                Particle.REDSTONE,
                                loc,
                                20,
                                0.3, 0.3, 0.3,
                                dustOptions
                        );
                    } else {
                        task.cancel();
                    }
                }, 1L, 2L);

                doubleJumped.put(player, true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.isOnGround()) {
            player.setAllowFlight(true);
            doubleJumped.put(player, false);
        }
    }

    @EventHandler
    public void chatFormat(AsyncPlayerChatEvent event){
        Player p = event.getPlayer();
        if (!p.isOp()) {
            event.setFormat(ChatColor.AQUA + p.getDisplayName() + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + event.getMessage());
        } else {
            event.setFormat(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "ADMIN" + ChatColor.DARK_AQUA + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + event.getMessage());
        }
    }
}