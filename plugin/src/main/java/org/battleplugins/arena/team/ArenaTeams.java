package org.battleplugins.arena.team;

import org.battleplugins.arena.config.ArenaOption;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public final class ArenaTeams implements Iterable<ArenaTeam> {
    // Built-in team when team selection is disabled or everyone is on their own team
    // (i.e. FFA, Arena, etc.)
    public static final ArenaTeam DEFAULT = new ArenaTeam("Default", Color.WHITE, new ItemStack(Material.WHITE_WOOL));

    @ArenaOption(name = "teams", description = "All of the registered teams.", required = true)
    private List<ArenaTeam> teams;

    @NotNull
    @Override
    public Iterator<ArenaTeam> iterator() {
        return this.teams.iterator();
    }
}
