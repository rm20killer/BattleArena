package org.battleplugins.arena.event.action.types;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public class BroadcastAction extends EventAction {
    private static final String AUDIENCE_KEY = "audience";
    private static final String MESSAGE_KEY = "message";
    private static final String TYPE_KEY = "type";

    public BroadcastAction(Map<String, String> params) {
        super(params, MESSAGE_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
        if (!(competition instanceof LiveCompetition<?> liveCompetition)) {
            return;
        }

        String message = this.get(MESSAGE_KEY);
        MessageType messageType = MessageType.valueOf(this.getOrDefault(TYPE_KEY, MessageType.CHAT.name())
                .toUpperCase(Locale.ROOT)
        );

        Audience audience = Audience.valueOf(this.getOrDefault(AUDIENCE_KEY, Audience.GAME.name())
                .toUpperCase(Locale.ROOT)
        );

        Collection<? extends Player> players = switch (audience) {
            case GAME -> liveCompetition.getPlayers().stream().map(ArenaPlayer::getPlayer).toList();
            case SERVER -> Bukkit.getOnlinePlayers();
        };

        for (Player player : players) {
            Component component = resolvable.resolve().resolveToComponent(MiniMessage.miniMessage().deserialize(message));
            switch (messageType) {
                case CHAT -> player.sendMessage(component);
                case ACTION_BAR -> player.sendActionBar(component);
                case TITLE -> player.showTitle(Title.title(component, Component.empty()));
                case SUBTITLE -> player.showTitle(Title.title(Component.empty(), component));
            }
        }
    }

    enum Audience {
        SERVER,
        GAME
    }

    enum MessageType {
        CHAT,
        ACTION_BAR,
        TITLE,
        SUBTITLE
    }
}
