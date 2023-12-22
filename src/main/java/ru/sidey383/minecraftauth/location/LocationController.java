package ru.sidey383.minecraftauth.location;

import ru.sidey383.minecraftauth.user.User;

/**
 * Интерфейс контейнера локаций. Сохраняет последную локацию пользователя и предоставляет способ её восстонавления.
 * **/
public interface LocationController {

    void restoreLocation(User player);

    void saveLocation(User player);

}
