package ru.sidey383.minecraftauth.user;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.sidey383.minecraftauth.CallBack;

import java.util.UUID;

/**
 * Обычный пользователь. Полностью реализует интерфейс User
 * **/
public class SimpleUser implements User {

    private final Player player;
    private final MessageListener listener;

    public SimpleUser(Player player, MessageListener listener) {
        this.player = player;
        this.listener = listener;
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        player.sendMessage(component);
    }

    @Override
    public void acceptMessage(@NotNull CallBack<String> text) {
        listener.acceptMessage(player.getUniqueId(), text);
    }

    @Override
    public @NotNull UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User user))
            return false;
        return player.getUniqueId().equals(user.getUUID());
    }

    @Override
    public int hashCode() {
        return player.getUniqueId().hashCode();
    }
}
