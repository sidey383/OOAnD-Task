package ru.sidey383.minecraftauth.user;

import ru.sidey383.minecraftauth.CallBack;

import java.util.UUID;

public interface MessageListener {

    void acceptMessage(UUID uuid, CallBack<String> callBack);

}
