package org.battleplugins.arena.command;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.JoinResult;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.PlayerRole;
import org.battleplugins.arena.competition.map.CompetitionMap;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.phase.CompetitionPhase;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.competition.phase.PhaseManager;
import org.battleplugins.arena.editor.ArenaEditorWizards;
import org.battleplugins.arena.editor.WizardStage;
import org.battleplugins.arena.editor.context.MapCreateContext;
import org.battleplugins.arena.editor.type.MapOption;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;
import org.battleplugins.arena.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ArenaCommandExecutor extends BaseCommandExecutor {
    protected final Arena arena;

    public ArenaCommandExecutor(Arena arena) {
        this(arena.getName().toLowerCase(Locale.ROOT), arena);
    }

    public ArenaCommandExecutor(String parentCommand, Arena arena) {
        super(parentCommand, arena.getName().toLowerCase(Locale.ROOT));

        this.arena = arena;
    }

    @ArenaCommand(commands = { "join", "j" }, description = "Join an arena.", permissionNode = "join")
    public void join(Player player) {
        List<LiveCompetitionMap> maps = this.arena.getPlugin().getMaps(this.arena);
        if (maps.isEmpty()) {
            Messages.NO_OPEN_ARENAS.send(player);
            return;
        }

        this.join(player, maps.iterator().next());
    }

    @ArenaCommand(commands = { "join", "j" }, description = "Join an arena.", permissionNode = "join")
    public void join(Player player, @Argument(name = "map") CompetitionMap map) {
        if (ArenaPlayer.getArenaPlayer(player) != null) {
            Messages.ALREADY_IN_ARENA.send(player);
            return;
        }

        if (map == null) {
            Messages.NO_ARENA_WITH_NAME.send(player);
            return;
        }

        List<Competition<?>> competitions = this.arena.getPlugin().getCompetitions(this.arena, map.getName());
        this.arena.getPlugin().findJoinableCompetition(competitions, player, PlayerRole.PLAYING).whenCompleteAsync((result, e) -> {
            if (e != null) {
                Messages.ARENA_ERROR.send(player, e.getMessage());
                this.arena.getPlugin().error("An error occurred while joining the arena", e);
                return;
            }

            Competition<?> competition = result.competition();
            if (competition != null) {
                competition.join(player, PlayerRole.PLAYING);

                Messages.ARENA_JOINED.send(player, competition.getMap().getName());
            } else {
                // Try and create a dynamic competition if possible
                this.arena.getPlugin()
                        .getOrCreateCompetition(this.arena, player, PlayerRole.PLAYING, map.getName())
                        .whenComplete((newResult, ex) -> {
                            if (ex != null) {
                                Messages.ARENA_ERROR.send(player, ex.getMessage());
                                this.arena.getPlugin().error("An error occurred while joining the arena", ex);
                                return;
                            }

                            if (newResult.competition() == null) {
                                // No competition - something happened that stopped the
                                // dynamic arena from being created. Not much we can do here,
                                // but info will be in console in the event of an error
                                if (newResult.result() != JoinResult.NOT_JOINABLE && newResult.result().message() != null) {
                                    newResult.result().message().send(player);
                                } else if (result.result() != JoinResult.NOT_JOINABLE && result.result().message() != null) {
                                   result.result().message().send(player);
                                } else {
                                    Messages.ARENA_NOT_JOINABLE.send(player);
                                }

                                return;
                            }

                            newResult.competition().join(player, PlayerRole.PLAYING);
                            Messages.ARENA_JOINED.send(player, newResult.competition().getMap().getName());
                        });
            }
        }, Bukkit.getScheduler().getMainThreadExecutor(this.arena.getPlugin()));
    }

    @ArenaCommand(commands = "kick", description = "Kick a player from the arena.", permissionNode = "kick")
    public void kick(Player player, Player target) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(target);
        if (arenaPlayer == null) {
            Messages.NOT_IN_ARENA.send(player);
            return;
        }

        if (player == target) {
            Messages.ARENA_CANNOT_KICK_SELF.send(player, this.parentCommand);
            return;
        }

        arenaPlayer.getCompetition().leave(target, ArenaLeaveEvent.Cause.KICKED);
        Messages.ARENA_KICKED.send(player, target.getName());
        Messages.ARENA_KICKED_PLAYER.send(target);
    }

    @ArenaCommand(commands = { "spectate", "s" }, description = "Spectate an arena.", permissionNode = "spectate")
    public void spectate(Player player) {
        List<Competition<?>> competitions = this.arena.getPlugin().getCompetitions(this.arena);
        if (competitions.isEmpty()) {
            Messages.NO_OPEN_ARENAS.send(player);
            return;
        }

        Competition<?> competition = competitions.iterator().next();
        this.spectate(player, competition);
    }

    @ArenaCommand(commands = { "spectate", "s" }, description = "Spectate an arena.", permissionNode = "spectate")
    public void spectate(Player player, Competition<?> competition) {
        if (ArenaPlayer.getArenaPlayer(player) != null) {
            Messages.ALREADY_IN_ARENA.send(player);
            return;
        }

        if (competition == null) {
            Messages.NO_ARENA_WITH_NAME.send(player);
            return;
        }

        competition.canJoin(player, PlayerRole.SPECTATING).whenComplete((result, e) -> {
            if (e != null) {
                Messages.ARENA_ERROR.send(player, e.getMessage());
                this.arena.getPlugin().error("An error occurred while spectating the arena", e);
                return;
            }

            if (result.canJoin()) {
                competition.join(player, PlayerRole.SPECTATING);

                Messages.ARENA_SPECTATE.send(player, competition.getMap().getName());
            } else {
                if (result.message() != null) {
                    result.message().send(player);
                } else {
                    Messages.ARENA_NOT_SPECTATABLE.send(player);
                }
            }
        });
    }

    @ArenaCommand(commands = { "leave", "l" }, description = "Leave an arena.", permissionNode = "leave")
    public void leave(Player player) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
        if (arenaPlayer == null) {
            Messages.NOT_IN_ARENA.send(player);
            return;
        }

        arenaPlayer.getCompetition().leave(player, ArenaLeaveEvent.Cause.COMMAND);
        Messages.ARENA_LEFT.send(player, arenaPlayer.getCompetition().getMap().getName());
    }

    @ArenaCommand(commands = "create", description = "Create a new arena.", permissionNode = "create")
    public void create(Player player) {
        ArenaEditorWizards.MAP_CREATION.openWizard(player, this.arena);
    }
    
    @ArenaCommand(commands = { "remove", "delete" }, description = "Removes an arena.", permissionNode = "remove")
    public void remove(Player player, CompetitionMap map) {
        if (!(map instanceof LiveCompetitionMap liveMap)) {
            Messages.NO_ARENA_WITH_NAME.send(player);
            return;
        }

        List<Competition<?>> activeCompetitions = this.arena.getPlugin().getCompetitions(this.arena, map.getName());
        for (Competition<?> activeCompetition : activeCompetitions) {
            // Empty out the competition
            if (activeCompetition instanceof LiveCompetition<?> competition) {
                for (ArenaPlayer arenaPlayer : Set.copyOf(competition.getPlayers())) {
                    competition.leave(arenaPlayer.getPlayer(), ArenaLeaveEvent.Cause.REMOVED);
                }
            }
        }

        this.arena.getPlugin().removeArenaMap(this.arena, liveMap);
        Messages.ARENA_REMOVED.send(player, map.getName());
    }

    @ArenaCommand(commands = "edit", description = "Edit an arena map.", permissionNode = "edit")
    public void map(Player player, CompetitionMap map, MapOption option) {
        if (!(map instanceof LiveCompetitionMap liveMap)) {
            Messages.NO_ARENA_WITH_NAME.send(player);
            return;
        }

        WizardStage<MapCreateContext> stage = ArenaEditorWizards.MAP_CREATION.getStage(option);
        ArenaEditorWizards.MAP_CREATION.openSingleWizardStage(player, this.arena, stage, context -> context.reconstructFrom(liveMap));
    }

    @ArenaCommand(commands = "advance", description = "Advances to arena to the next phase.", permissionNode = "advance")
    public void advance(Player player) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
        if (arenaPlayer == null) {
            Messages.NOT_IN_ARENA.send(player);
            return;
        }

        LiveCompetition<?> competition = arenaPlayer.getCompetition();
        PhaseManager<?> phaseManager = competition.getPhaseManager();
        CompetitionPhaseType<?, ? extends CompetitionPhase<?>> nextPhase = phaseManager.getCurrentPhase().getNextPhase();
        if (nextPhase != null) {
            Messages.ADVANCED_PHASE.send(player, nextPhase.getName());

            phaseManager.setPhase(nextPhase);
        } else {
            Messages.NO_PHASES.send(player);
        }
    }

    @Override
    protected Object onVerifyArgument(CommandSender sender, String arg, Class<?> parameter) {
        switch (parameter.getSimpleName().toLowerCase()) {
            case "competition" -> {
                List<Competition<?>> openCompetitions = this.arena.getPlugin().getCompetitions(this.arena, arg);
                if (openCompetitions.isEmpty()) {
                    return null;
                }

                return openCompetitions.get(0);
            }
            case "competitionmap" -> {
                return this.arena.getPlugin().getMap(this.arena, arg);
            }
        }

        return super.onVerifyArgument(sender, arg, parameter);
    }

    @Override
    protected boolean onInvalidArgument(CommandSender sender, Class<?> parameter, String input) {
        switch (parameter.getSimpleName().toLowerCase()) {
            case "competition", "competitionmap" -> {
                Messages.NO_ARENA_WITH_NAME.send(sender);
                return true;
            }
        }

        return super.onInvalidArgument(sender, parameter, input);
    }

    @Override
    protected List<String> onVerifyTabComplete(String arg, Class<?> parameter) {
        if (parameter.getSimpleName().equalsIgnoreCase("competition")) {
            return this.arena.getPlugin().getCompetitions(this.arena)
                    .stream()
                    .map(competition -> competition.getMap().getName())
                    .toList();
        } else if (parameter.getSimpleName().equalsIgnoreCase("competitionmap")) {
            return this.arena.getPlugin().getMaps(this.arena)
                    .stream()
                    .map(CompetitionMap::getName)
                    .distinct()
                    .toList();
        }

        return super.onVerifyTabComplete(arg, parameter);
    }

    @Override
    public final void sendHeader(CommandSender sender) {
        Messages.HEADER.sendCentered(sender, this.arena.getName());
    }
}
