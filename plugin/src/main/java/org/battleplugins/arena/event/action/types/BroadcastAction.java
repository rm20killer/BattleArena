package org.battleplugins.arena.event.action.types;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.event.action.EventAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

public class BroadcastAction extends EventAction {
    private static final String MESSAGE_KEY = "message";
    private static final String TYPE_KEY = "type";

    public BroadcastAction(Map<String, String> params) {
        super(params, MESSAGE_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer) {
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition) {
        String message = this.get(MESSAGE_KEY);
        MessageType messageType = MessageType.valueOf(this.getOrDefault(TYPE_KEY, MessageType.CHAT.name())
                .toUpperCase(Locale.ROOT)
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            Component component = MiniMessage.miniMessage().deserialize(message);
            switch (messageType) {
                case CHAT -> player.sendMessage(component);
                case ACTION_BAR -> player.sendActionBar(component);
                case TITLE -> player.showTitle(Title.title(component, Component.empty()));
                case SUBTITLE -> player.showTitle(Title.title(Component.empty(), component));
            }
        }
    }

    enum MessageType {
        CHAT,
        ACTION_BAR,
        TITLE,
        SUBTITLE
    }
}
