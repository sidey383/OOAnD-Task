package ru.sidey383.minecraftauth.database;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.sidey383.minecraftauth.core.AuthorizationModule;
import ru.sidey383.minecraftauth.user.User;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDatabase implements AuthorizationDatabase {

    private final Logger logger;

    private final File dir;

    public FileDatabase(File dir, Logger logger) {
        this.logger = logger;
        this.dir = dir;
    }

    @Override
    public <T extends AuthorizationModule> Optional<AuthorizationRecord<T>> getRecord(User user, Class<T> type) {
        File f = new File(dir, user.getUUID().toString());
        if (!f.exists())
            return Optional.empty();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(f);
        if(!configuration.contains(type.getCanonicalName())) {
            return Optional.empty();
        }
        String data = configuration.getString(type.getCanonicalName());
        return Optional.of(new SimpleAuthorizationRecord<>(type, data));
    }

    @Override
    public <T extends AuthorizationModule> void setRecord(User user, AuthorizationRecord<T> record) {
        File f = new File(dir, user.getUUID().toString());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(f);
        configuration.set(record.getType().getCanonicalName(), record.getData());
        try {
            configuration.save(f);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "File save error", e);
        }
    }
}
