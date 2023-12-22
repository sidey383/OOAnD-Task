package ru.sidey383.minecraftauth.user;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ru.sidey383.minecraftauth.CallBack;

import java.util.UUID;

/**
 * Описание унифиированного пользователя
 * **/
public interface User {


    /**
     * Отправить сообщение пользователю
     * **/
    void sendMessage(@NotNull Component component);

    /**
     * Принять сообщение от пользователя
     * **/
    void acceptMessage(@NotNull CallBack<String> text);

    /**
     * Уникальный идентификатор пользователя
     * **/
    @NotNull
    UUID getUUID();

}
