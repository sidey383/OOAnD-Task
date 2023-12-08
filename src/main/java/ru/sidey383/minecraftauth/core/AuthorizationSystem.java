package ru.sidey383.minecraftauth.core;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import ru.sidey383.minecraftauth.location.LocationController;
import ru.sidey383.minecraftauth.user.User;
import ru.sidey383.minecraftauth.user.UserFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthorizationSystem implements Listener {

    private final Logger logger;

    private final UserFactory userFactory;

    private final LocationController locationController;

    private final Collection<AuthorizationModule> modules;

    private final Map<User, AuthorizationModule> authorizations = new ConcurrentHashMap<>();

    public AuthorizationSystem(
            Plugin plugin,
            UserFactory userFactory,
            LocationController locationController,
            Collection<AuthorizationModule> modules) {
        logger = plugin.getLogger();
        this.userFactory = userFactory;
        this.locationController = locationController;
        this.modules = Collections.unmodifiableCollection(modules);
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
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
                module.loginUser(user, (status) -> {
                    authorizations.remove(user);
                    switch (status) {
                        case Error -> pl.kick(Component.text("Authorization error"));
                        case Authorized -> locationController.restoreLocation(user);
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
                case Registered -> locationController.restoreLocation(user);
            }
        });
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e) {
        User user = userFactory.createUser(e.getPlayer());
        AuthorizationModule module = authorizations.remove(user);
        module.abort(user);
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
        while (i.hasNext()) {
            var p = i.next();
            p.getValue().abort(p.getKey());
            i.remove();
        }
    }

}
