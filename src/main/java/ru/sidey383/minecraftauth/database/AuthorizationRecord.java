package ru.sidey383.minecraftauth.database;

import ru.sidey383.minecraftauth.core.AuthorizationModule;

/**
 * Запись в базе данных для конкретного метода авторизации. <p/>
 * **/
public interface AuthorizationRecord<T extends AuthorizationModule> {

    /**
     * Должен возвращать класс модуля с которой связана запись
     * **/
    Class<T> getType();

    String getData();

}
