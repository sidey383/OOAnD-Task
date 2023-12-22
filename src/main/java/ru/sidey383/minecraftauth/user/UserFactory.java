package ru.sidey383.minecraftauth.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Фабрика пользователей
 * **/
public interface UserFactory {

    @NotNull
    User createUser(@NotNull Player player);

}
