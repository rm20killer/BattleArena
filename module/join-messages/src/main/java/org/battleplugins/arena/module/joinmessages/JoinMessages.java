package org.battleplugins.arena.module.joinmessages;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.event.arena.ArenaInitializeEvent;
import org.battleplugins.arena.event.player.ArenaJoinEvent;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.bukkit.event.EventHandler;

/**
 * A module that adds join messages to the arena.
 */
@ArenaModule(id = JoinMessages.ID, name = "Join Messages", description = "Adds join messages to BattleArena.", authors = "BattlePlugins")
public class JoinMessages implements ArenaModuleInitializer, ArenaListener {
    public static final String ID = "join-messages";

    private static final Message PLAYER_JOINED = Messages.info("join-messages-player-joined", "<primary>{}</primary> has joined the game <aqua>({}/{})</aqua>.");
    private static final Message PLAYER_JOINED_NO_LIMIT = Messages.info("join-messages-player-joined-no-limit", "<primary>{}</primary> has joined the game.");
    private static final Message PLAYER_LEFT = Messages.info("join-messages-player-left", "<primary>{}</primary> has left the game <aqua>({}/{})</aqua>.");
    private static final Message PLAYER_LEFT_NO_LIMIT = Messages.info("join-messages-player-left-no-limit", "<primary>{}</primary> has left the game.");

    @EventHandler
    public void onArenaInitialize(ArenaInitializeEvent event) {
        if (!event.getArena().isModuleEnabled(ID)) {
            return;
        }

        event.getArena().getEventManager().registerEvents(this);
    }

    @ArenaEventHandler
    public void onJoin(ArenaJoinEvent event) {
        LiveCompetition<?> competition = event.getCompetition();
        boolean hasMax = competition.getArena().getTeams().hasMaxPlayers();
        for (ArenaPlayer player : competition.getPlayers()) {
            if (hasMax) {
                PLAYER_JOINED.send(
                        player.getPlayer(),
                        event.getPlayer().getName(),
                        Integer.toString(competition.getPlayers().size()),
                        Integer.toString(competition.getMaxPlayers())
                );
            } else {
                PLAYER_JOINED_NO_LIMIT.send(player.getPlayer(), event.getPlayer().getName());
            }
        }
    }

    @ArenaEventHandler
    public void onLeave(ArenaLeaveEvent event) {
        if (event.getCause() == ArenaLeaveEvent.Cause.SHUTDOWN || event.getCause() == ArenaLeaveEvent.Cause.GAME) {
            return;
        }

        LiveCompetition<?> competition = event.getCompetition();
        boolean hasMax = competition.getArena().getTeams().hasMaxPlayers();
        for (ArenaPlayer player : competition.getPlayers()) {
            if (hasMax) {
                PLAYER_LEFT.send(
                        player.getPlayer(),
                        event.getPlayer().getName(),
                        Integer.toString(competition.getPlayers().size()),
                        Integer.toString(competition.getMaxPlayers())
                );
            } else {
                PLAYER_LEFT_NO_LIMIT.send(player.getPlayer(), event.getPlayer().getName());
            }
        }
    }
}
