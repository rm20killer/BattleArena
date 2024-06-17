package org.battleplugins.arena.options;

import org.battleplugins.arena.config.ArenaOption;

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
