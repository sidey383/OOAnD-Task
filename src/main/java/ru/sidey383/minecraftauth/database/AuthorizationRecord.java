package ru.sidey383.minecraftauth.database;

import ru.sidey383.minecraftauth.core.AuthorizationModule;

public interface AuthorizationRecord<T extends AuthorizationModule> {

    Class<T> getType();

    String getData();

}
