package ru.sidey383.minecraftauth.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SimpleUserFactory implements UserFactory {

    private final MessageListener listener;

    public SimpleUserFactory(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public @NotNull User createUser(@NotNull Player player) {
        return new SimpleUser(player, listener);
    }
}
