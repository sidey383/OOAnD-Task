package ru.sidey383.minecraftauth.core;

import ru.sidey383.minecraftauth.CallBack;
import ru.sidey383.minecraftauth.database.AuthorizationDatabase;
import ru.sidey383.minecraftauth.user.User;

/**
 * Интерфейс, описывающий любой метод авторизации.
 * **/
public interface AuthorizationModule {

    void registerUser(User user, CallBack<RegistrationStatus> callback);

    void loginUser(User user, CallBack<AuthorizationStatus> callback);

    void abort(User user);

    boolean isRegistered(User user);

    /**
     * Устанавливается в первую очередь.
     * База данных в которой метод авторизации может хранить свои данные.
     * **/
    void setDatabase(AuthorizationDatabase database);

}
