package org.battleplugins.arena.module.tournaments;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.command.ArenaCommand;
import org.battleplugins.arena.command.BaseCommandExecutor;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;
import org.battleplugins.arena.messages.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class TournamentCommandExecutor extends BaseCommandExecutor {
    private final Tournaments tournaments;

    public TournamentCommandExecutor(String parentCommand, Tournaments tournaments) {
        super(parentCommand, parentCommand);

        this.tournaments = tournaments;
    }

    @ArenaCommand(commands = "create", description = "Creates a tournament in an arena.", permissionNode = "create")
    public void create(Player player, Arena arena) {
        try {
            this.tournaments.createTournament(arena);

            TournamentMessages.TOURNAMENT_CREATED.send(player, arena.getName());
        } catch (TournamentException e) {
            e.getErrorMessage().send(player);
        }
    }

    @ArenaCommand(commands = "start", description = "Starts a tournament in an arena.", permissionNode = "start")
    public void start(Player player, Arena arena) {
        try {
            this.tournaments.startTournament(arena);

            TournamentMessages.TOURNAMENT_STARTED.send(player, arena.getName());
        } catch (TournamentException e) {
            e.getErrorMessage().send(player);
        }
    }

    @ArenaCommand(commands = "end", description = "Ends a tournament in an arena.", permissionNode = "end")
    public void end(Player player, Arena arena) {
        try {
            this.tournaments.endTournament(arena);

            TournamentMessages.TOURNAMENT_ENDED.send(player, arena.getName());
        } catch (TournamentException e) {
            e.getErrorMessage().send(player);
        }
    }

    @ArenaCommand(commands = "list", description = "Lists all active tournaments.", permissionNode = "list")
    public void list(Player player) {
        List<Tournament> tournaments = this.tournaments.getActiveTournaments();
        if (tournaments.isEmpty()) {
            TournamentMessages.TOURNAMENT_NO_ACTIVE_TOURNAMENTS.send(player);
            return;
        }

        Messages.HEADER.send(player, "Tournaments");
        tournaments.forEach(tournament -> {
            player.sendMessage(Component.text("- ", NamedTextColor.GRAY).append(Component.text(tournament.getArena().getName(), Messages.PRIMARY_COLOR)));
        });
    }

    @ArenaCommand(commands = { "join", "j" }, description = "Joins a tournament in an arena.", permissionNode = "join")
    public void join(Player player, Arena arena) {
        try {
            Tournament tournament = this.tournaments.getTournament(arena);
            if (tournament == null) {
                TournamentMessages.TOURNAMENT_NOT_FOUND.send(player, arena.getName());
                return;
            }

            if (tournament.isInTournament(player)) {
                TournamentMessages.TOURNAMENT_ALREADY_JOINED.send(player);
                return;
            }

            if (arena.getPlugin().isInArena(player)) {
                TournamentMessages.TOURNAMENT_CANNOT_JOIN_TOURNAMENT_IN_ARENA.send(player);
                return;
            }

            List<Tournament> activeTournaments = this.tournaments.getActiveTournaments();
            for (Tournament activeTournament : activeTournaments) {
                if (activeTournament.isInTournament(player)) {
                    TournamentMessages.TOURNAMENT_IN_OTHER_TOURNAMENT.send(player);
                    return;
                }
            }

            if (tournament.hasStarted()) {
                TournamentMessages.TOURNAMENT_ALREADY_STARTED.send(player);
                return;
            }

            tournament.join(player);
            TournamentMessages.TOURNAMENT_JOINED.send(player, arena.getName());
        } catch (TournamentException e) {
            e.getErrorMessage().send(player);
        }
    }

    @ArenaCommand(commands = { "leave", "l" }, description = "Leaves a tournament from an arena.", permissionNode = "leave")
    public void leave(Player player, Arena arena) {
        try {
            Tournament tournament = this.tournaments.getTournament(arena);
            if (tournament == null) {
                TournamentMessages.TOURNAMENT_NOT_FOUND.send(player, arena.getName());
                return;
            }

            if (!tournament.isInTournament(player)) {
                TournamentMessages.TOURNAMENT_NOT_IN_TOURNAMENT.send(player);
                return;
            }

            tournament.leave(player);
            ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
            if (arenaPlayer != null) {
                arenaPlayer.getCompetition().leave(player, ArenaLeaveEvent.Cause.COMMAND);
            }

            TournamentMessages.TOURNAMENT_LEFT.send(player, arena.getName());
        } catch (TournamentException e) {
            e.getErrorMessage().send(player);
        }
    }
}
