package ru.sidey383.minecraftauth.database;

import ru.sidey383.minecraftauth.core.AuthorizationModule;
import ru.sidey383.minecraftauth.user.User;

import java.util.Optional;

public interface AuthorizationDatabase {

    <T extends AuthorizationModule> Optional<AuthorizationRecord<T>> getRecord(User user, Class<T> type);

    <T extends AuthorizationModule> void setRecord(User user, AuthorizationRecord<T> record);

}
