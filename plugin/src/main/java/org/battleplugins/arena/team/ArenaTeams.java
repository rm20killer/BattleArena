package org.battleplugins.arena.team;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/teams")
public final class ArenaTeams implements Iterable<ArenaTeam> {
    // Built-in team when team selection is disabled or everyone is on their own team
    // (i.e. FFA, Arena, etc.)
    public static final ArenaTeam DEFAULT = new ArenaTeam("Default", Color.WHITE, new ItemStack(Material.WHITE_WOOL));

    @ArenaOption(name = "teams", description = "All of the registered teams.", required = true)
    private List<ArenaTeam> teams;

    public Optional<ArenaTeam> team(String name) {
        return Optional.ofNullable(this.getTeam(name));
    }

    @Nullable
    public ArenaTeam getTeam(String name) {
        for (ArenaTeam team : this.teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }

        return null;
    }

    @NotNull
    @Override
    public Iterator<ArenaTeam> iterator() {
        return this.teams.iterator();
    }
}
