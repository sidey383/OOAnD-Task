package ru.sidey383.minecraftauth.user;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ru.sidey383.minecraftauth.CallBack;

import java.util.UUID;

public interface User {


    void sendMessage(@NotNull Component component);

    void acceptMessage(@NotNull CallBack<String> text);

    @NotNull
    UUID getUUID();

}
