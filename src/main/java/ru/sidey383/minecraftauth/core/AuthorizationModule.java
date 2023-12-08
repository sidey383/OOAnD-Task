package ru.sidey383.minecraftauth.core;

import ru.sidey383.minecraftauth.CallBack;
import ru.sidey383.minecraftauth.database.AuthorizationDatabase;
import ru.sidey383.minecraftauth.user.User;

public interface AuthorizationModule {

    void registerUser(User user, CallBack<RegistrationStatus> callback);

    void loginUser(User user, CallBack<AuthorizationStatus> callback);

    void abort(User user);

    boolean isRegistered(User user);

    void setDatabase(AuthorizationDatabase database);

}
