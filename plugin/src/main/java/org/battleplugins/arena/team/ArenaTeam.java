package org.battleplugins.arena.team;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/teams")
public class ArenaTeam {

    @ArenaOption(name = "name", description = "The name of the team.", required = true)
    private String name;

    @ArenaOption(name = "color", description = "The color of the team.", required = true)
    private Color color;

    @ArenaOption(name = "item", description = "The item representing the team.", required = true)
    private ItemStack item;

    public ArenaTeam() {
    }

    public ArenaTeam(String name, Color color, ItemStack item) {
        this.name = name;
        this.color = color;
        this.item = item;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public boolean isHostileTo(ArenaTeam team) {
        if (this == ArenaTeams.DEFAULT && team == ArenaTeams.DEFAULT) {
            return true;
        }

        // TODO: Check additional game options to see if teams are hostile to each other
        return !team.equals(this);
    }

    @Override
    public String toString() {
        return "ArenaTeam{" +
                "name='" + this.name + '\'' +
                ", color=" + this.color +
                ", item=" + this.item +
                '}';
    }
}
