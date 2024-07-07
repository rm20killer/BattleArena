package org.battleplugins.arena.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;

/**
 * Represents a team in an arena.
 */
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

    /**
     * Returns the name of the team.
     *
     * @return the name of the team
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the color of the team.
     *
     * @return the color of the team
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Returns the {@link TextColor} of the team.
     *
     * @return the text color of the team
     */
    public TextColor getTextColor() {
        return TextColor.color(this.color.getRGB());
    }

    /**
     * Returns the formatted name of the team.
     *
     * @return the formatted name of the team
     */
    public Component getFormattedName() {
        return Component.text(this.name).color(this.getTextColor());
    }

    /**
     * Returns the {@link ItemStack} representing the team.
     *
     * @return the item representing the team
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Returns whether this team is hostile to the given team.
     *
     * @param team the team to check hostility against
     * @return whether this team is hostile to the given team
     */
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
