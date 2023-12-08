package ru.sidey383.minecraftauth.module.password;

import ru.sidey383.minecraftauth.database.AuthorizationRecord;

public record PasswordRecord(
        String password
) implements AuthorizationRecord<PasswordAuthorizationModule> {
    @Override
    public Class<PasswordAuthorizationModule> getType() {
        return PasswordAuthorizationModule.class;
    }

    @Override
    public String getData() {
        return password;
    }
}
