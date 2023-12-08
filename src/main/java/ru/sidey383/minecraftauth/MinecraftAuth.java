package ru.sidey383.minecraftauth;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import ru.sidey383.minecraftauth.core.AuthorizationModule;
import ru.sidey383.minecraftauth.core.AuthorizationSystem;
import ru.sidey383.minecraftauth.database.FileDatabase;
import ru.sidey383.minecraftauth.location.FileLocationController;
import ru.sidey383.minecraftauth.module.password.PasswordAuthorizationModule;
import ru.sidey383.minecraftauth.user.MinecraftMessageListener;
import ru.sidey383.minecraftauth.user.SimpleUserFactory;
import ru.sidey383.minecraftauth.user.UserFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;

@Plugin(
        name = "MinecraftAuth",
        version = "1.0-SNAPSHOT"
)
public class MinecraftAuth extends JavaPlugin {

    private AuthorizationSystem system;

    private MinecraftMessageListener listener;

    @Override
    public void onEnable() {
        File dbDir = new File(getDataFolder(), "database");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        FileDatabase database = new FileDatabase(dbDir, getLogger());
        File locDir = new File(getDataFolder(), "locations");
        if (!locDir.exists()) {
            locDir.mkdirs();
        }
        FileLocationController locationController = new FileLocationController(locDir, getLogger());
        Collection<AuthorizationModule> modules = List.of(new PasswordAuthorizationModule());
        modules.forEach(m -> m.setDatabase(database));
        listener = new MinecraftMessageListener(this);
        UserFactory factory = new SimpleUserFactory(listener);
        system = new AuthorizationSystem(this, factory, locationController, modules);
    }

    @Override
    public void onDisable() {
        system.disable();
        listener.cancelAll();
    }
}
