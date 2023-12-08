package ru.sidey383.minecraftauth.location;

import ru.sidey383.minecraftauth.user.User;

public interface LocationController {

    void restoreLocation(User player);

    void saveLocation(User player);

}
