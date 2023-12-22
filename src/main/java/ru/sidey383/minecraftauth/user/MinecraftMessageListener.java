package ru.sidey383.minecraftauth.user;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.sidey383.minecraftauth.CallBack;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Слушатель сообщений игрока. Реализует взаимодействие с игроком через чат.
 * **/
public class MinecraftMessageListener implements MessageListener, Listener {

    private final Map<UUID, List<CallBack<String>>> values = new ConcurrentHashMap<>();

    public MinecraftMessageListener(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void acceptMessage(UUID uuid, CallBack<String> callBack) {
        List<CallBack<String>> list = values.getOrDefault(uuid, new ArrayList<>());
        list.add(callBack);
        values.put(uuid, list);
    }

    public void cancelAll() {
        values.clear();
    }

    @EventHandler
    public void onMessageSend(AsyncPlayerChatEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        List<CallBack<String>> cb = values.remove(uuid);
        if (cb != null) {
            e.setCancelled(true);
            cb.forEach(c -> c.apply(e.getMessage()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        List<CallBack<String>> cb = values.remove(uuid);
        if (cb != null) {
            cb.forEach(c -> c.apply(null));
        }
    }

}
