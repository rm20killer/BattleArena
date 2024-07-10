package org.battleplugins.arena.event.action.types;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Locale;
import java.util.Map;

public class SendMessageAction extends EventAction {
    private static final String MESSAGE_KEY = "message";
    private static final String TYPE_KEY = "type";

    public SendMessageAction(Map<String, String> params) {
        super(params, MESSAGE_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String message = this.get(MESSAGE_KEY);
        MessageType messageType = MessageType.valueOf(this.getOrDefault(TYPE_KEY, MessageType.CHAT.name())
                .toUpperCase(Locale.ROOT)
        );

        Component component = resolvable.resolve().resolveToComponent(MiniMessage.miniMessage().deserialize(message));
        switch (messageType) {
            case CHAT -> arenaPlayer.getPlayer().sendMessage(component);
            case ACTION_BAR -> arenaPlayer.getPlayer().sendActionBar(component);
            case TITLE -> arenaPlayer.getPlayer().showTitle(Title.title(component, Component.empty()));
            case SUBTITLE -> arenaPlayer.getPlayer().showTitle(Title.title(Component.empty(), component));
        }
    }

    enum MessageType {
        CHAT,
        ACTION_BAR,
        TITLE,
        SUBTITLE
    }
}
