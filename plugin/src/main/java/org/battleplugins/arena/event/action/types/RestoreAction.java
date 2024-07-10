package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.competition.PlayerStorage;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;
import java.util.Set;

public class RestoreAction extends EventAction {
    private static final String TYPES_KEY = "types";

    public RestoreAction(Map<String, String> params) {
        super(params, TYPES_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String[] types = this.get(TYPES_KEY).split(",");
        PlayerStorage.Type[] toStore = new PlayerStorage.Type[types.length];
        for (int i = 0; i < types.length; i++) {
            toStore[i] = PlayerStorage.Type.valueOf(types[i].toUpperCase());
        }

        arenaPlayer.getStorage().restore(Set.of(toStore));
    }
}
