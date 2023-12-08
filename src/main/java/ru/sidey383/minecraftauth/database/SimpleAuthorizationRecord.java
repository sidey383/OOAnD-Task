package ru.sidey383.minecraftauth.database;

import ru.sidey383.minecraftauth.core.AuthorizationModule;

public record SimpleAuthorizationRecord<T extends AuthorizationModule>
        (
                Class<T> type,
                String data
        ) implements AuthorizationRecord<T> {


    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getData() {
        return data;
    }
}
