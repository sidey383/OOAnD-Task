package ru.sidey383.minecraftauth.core;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import ru.sidey383.minecraftauth.location.LocationController;
import ru.sidey383.minecraftauth.user.User;
import ru.sidey383.minecraftauth.user.UserFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Основной класс, контролирующий процесс авторизации пользователей.
 * **/
public class AuthorizationSystem implements Listener {

    private final Logger logger;

    private final UserFactory userFactory;

    private final LocationController locationController;

    private final Collection<AuthorizationModule> modules;

    private final Map<User, AuthorizationModule> authorizations = new ConcurrentHashMap<>();

    private final Set<User> authorized = Collections.synchronizedSet(new HashSet<>());

    public AuthorizationSystem(
            Plugin plugin,
            UserFactory userFactory,
            LocationController locationController,
            Collection<AuthorizationModule> modules) {
        logger = plugin.getLogger();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.userFactory = userFactory;
        this.locationController = locationController;
        this.modules = Collections.unmodifiableCollection(modules);
    }

    /**
     * Реакция на вход игрока на сервер. Инициирует авторизацию.
     * **/
    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        Location loc = getAuthorizeLocation();
        Block b = loc.getBlock();
        if (b.isEmpty())
            b.setType(Material.BARRIER);
        pl.teleport(loc.add(0, 1, 0));
        if (modules.isEmpty()) {
            logger.log(Level.WARNING, "No auth methods!");
            return;
        }
        User user = userFactory.createUser(pl);
        if (authorizations.containsKey(user)) {
            pl.kick(Component.text("You are already logged in"));
            return;
        }
        for (AuthorizationModule module : modules) {
            if (module.isRegistered(user)) {
                authorizations.put(user, module);
                pl.teleport(loc.add(0, 1, 0));
                module.loginUser(user, (status) -> {
                    authorizations.remove(user);
                    switch (status) {
                        case Error -> pl.kick(Component.text("Authorization error"));
                        case Authorized -> {
                            authorized.add(user);
                            locationController.restoreLocation(user);
                        }
                        case NotAuthorized -> pl.kick(Component.text("Can't authorize"));
                    }
                });
                return;
            }
        }
        AuthorizationModule module = modules.iterator().next();
        authorizations.put(user, module);
        module.registerUser(user, (status) -> {
            authorizations.remove(user);
            switch (status) {
                case Error -> pl.kick(Component.text("Registration error"));
                case Registered -> {
                    authorized.add(user);
                    locationController.restoreLocation(user);
                }
            }
        });
    }

    public Location getAuthorizeLocation() {
        return new Location(Bukkit.getWorld("world"), 100000, 255, 100000);
    }

    /**
     * Реакция на выход игрока. Если игрок авторизовывался - авторизация отменяется.
     * **/
    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e) {
        User user = userFactory.createUser(e.getPlayer());
        AuthorizationModule module = authorizations.remove(user);
        if (module == null)
            return;
        module.abort(user);
        if (authorized.contains(user)) {
            locationController.saveLocation(user);
        }
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent e) {
        User user = userFactory.createUser(e.getPlayer());
        if (authorizations.containsKey(user)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player player))
            return;
        User user = userFactory.createUser(player);
        if (authorizations.containsKey(user)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        User user = userFactory.createUser(e.getPlayer());
        if (authorizations.containsKey(user)) {
            e.setCancelled(true);
        }
    }

    public void disable() {
        var i = authorizations.entrySet().iterator();
        HandlerList.unregisterAll(this);
        while (i.hasNext()) {
            var p = i.next();
            p.getValue().abort(p.getKey());
            i.remove();
        }
    }

}
