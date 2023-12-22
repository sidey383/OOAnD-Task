package ru.sidey383.minecraftauth.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.sidey383.minecraftauth.user.User;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileLocationController implements LocationController {

    private final Logger logger;

    private final Plugin plugin;

    private final File dir;

    public FileLocationController(File dir, Logger logger, Plugin plugin) {
        this.dir = dir;
        this.logger = logger;
        this.plugin = plugin;
    }


    @Override
    public void restoreLocation(User player) {
        Player pl = Bukkit.getPlayer(player.getUUID());
        File f = new File(dir, player.getUUID().toString());
        Location def = Objects.requireNonNull(Bukkit.getWorld("world"))
                .getHighestBlockAt(0, 0)
                .getLocation().
                add(0, 1, 0);
        if (pl == null)
            return;
        if (!f.exists()) {
            Bukkit.getScheduler().runTask(plugin, () -> pl.teleport(def));

        } else {
            Configuration configuration = YamlConfiguration.loadConfiguration(f);
            Location loc = configuration.getObject("location", Location.class, def);
            Bukkit.getScheduler().runTask(plugin, () -> pl.teleport(loc));
        }
    }

    @Override
    public void saveLocation(User player) {
        Player pl = Bukkit.getPlayer(player.getUUID());
        if (pl == null)
            return;
        File f = new File(dir, player.getUUID().toString());
        Location loc = pl.getLocation();
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("location", loc);
        try {
            configuration.save(f);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "File save error", e);
        }
    }
}
