package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;
import org.bukkit.GameMode;

import java.util.Locale;
import java.util.Map;

public class ChangeGamemodeAction extends EventAction {
    private static final String GAMEMODE_KEY = "gamemode";

    public ChangeGamemodeAction(Map<String, String> params) {
        super(params, GAMEMODE_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        arenaPlayer.getPlayer().setGameMode(GameMode.valueOf(this.get(GAMEMODE_KEY).toUpperCase(Locale.ROOT)));
    }
}
