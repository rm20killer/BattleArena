package org.battleplugins.arena.module.placeholderapi;

import org.battleplugins.arena.BattleArena;

public class PlaceholderApiContainer {
    private final BattleArenaExpansion expansion;

    public PlaceholderApiContainer(BattleArena plugin) {
        this.expansion = new BattleArenaExpansion(plugin);
        this.expansion.register();
    }

    public void disable() {
        this.expansion.unregister();
    }
}
