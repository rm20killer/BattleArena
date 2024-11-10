package org.battleplugins.arena.module.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.resolver.Resolver;
import org.battleplugins.arena.resolver.ResolverKey;
import org.battleplugins.arena.resolver.ResolverKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BattleArenaExpansion extends PlaceholderExpansion {
    private final BattleArena plugin;

    public BattleArenaExpansion(BattleArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ba";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BattlePlugins";
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] split = params.split("_");

        // No data for us to parse
        if (split.length < 2) {
            return null;
        }

        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
        if (arenaPlayer != null && params.startsWith("competition")) {
            String placeholder = String.join("_", split).substring("competition_".length());

            Resolver resolver = arenaPlayer.resolve();
            ResolverKey<?> resolverKey = ResolverKeys.get(placeholder.replace("_", "-"));
            if (resolverKey != null && resolver.has(resolverKey)) {
                return resolver.resolveToString(resolverKey);
            }
        }

        // If player is null or no other placeholder resolvers have made it to this point,
        // handle more general placeholders

        String arenaName = split[0];
        Arena arena = this.plugin.getArena(arenaName);

        // No arena, so not much we can do here
        if (arena == null) {
            return null;
        }

        // Remaining text in split array
        String placeholder = String.join("_", split).substring(arenaName.length() + 1);
        switch (placeholder) {
            case "active_competitions": {
                return String.valueOf(this.plugin.getCompetitions(arena).size());
            }
            case "online_players": {
                int players = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    players += competition.getAlivePlayerCount() + competition.getSpectatorCount();
                }

                return String.valueOf(players);
            }
            case "alive_players": {
                int online = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    online += competition.getAlivePlayerCount();
                }

                return String.valueOf(online);
            }
            case "spectators": {
                int spectators = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    spectators += competition.getSpectatorCount();
                }

                return String.valueOf(spectators);
            }
            case "waiting_competitions": {
                int waitingCompetitions = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    CompetitionPhaseType<?, ?> phase = competition.getPhase();
                    if (CompetitionPhaseType.WAITING.equals(phase)) {
                        waitingCompetitions++;
                    }
                }
                return String.valueOf(waitingCompetitions);
            }
            case "ingame_competitions": {
                int ingameCompetitions = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    CompetitionPhaseType<?, ?> phase = competition.getPhase();
                    if (CompetitionPhaseType.INGAME.equals(phase)) {
                        ingameCompetitions++;
                    }
                }
                return String.valueOf(ingameCompetitions);
            }
        }

        return null;
    }
}
