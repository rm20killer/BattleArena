package org.battleplugins.arena.options;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/chapter/configuration")
public class Lives {

    @ArenaOption(name = "enabled", description = "Whether or not lives are enabled.")
    private boolean enabled = false;

    @ArenaOption(name = "amount", description = "The amount of lives each player has.")
    private int lives = 1;

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getLives() {
        return this.lives;
    }
}
