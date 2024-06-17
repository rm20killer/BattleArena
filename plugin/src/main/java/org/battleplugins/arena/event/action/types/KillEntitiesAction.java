package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.map.options.Bounds;
import org.battleplugins.arena.config.SingularValueParser;
import org.battleplugins.arena.event.action.EventAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KillEntitiesAction extends EventAction {
    private static final String EXCLUDED_GROUPS = "excluded-groups";

    public KillEntitiesAction(Map<String, String> params, String... requiredKeys) {
        super(params, requiredKeys);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer) {
        Bounds bounds = arenaPlayer.getCompetition().getMap().getBounds();
        if (bounds == null) {
            return;
        }

        List<String> excludedGroups = new ArrayList<>();
        String groupStr = this.get(EXCLUDED_GROUPS);
        if (groupStr != null) {
            SingularValueParser.ArgumentBuffer buffer = SingularValueParser.parseUnnamed(groupStr, SingularValueParser.BraceStyle.SQUARE, ',');
            while (buffer.hasNext()) {
                SingularValueParser.Argument argument = buffer.pop();
                excludedGroups.add(argument.value());
            }
        }

        arenaPlayer.getPlayer().getWorld().getEntities().stream()
                .filter(entity -> bounds.isInside(entity.getLocation()))
                .filter(entity -> !(entity instanceof Player))
                .filter(entity -> excludedGroups.isEmpty() || !excludedGroups.contains(entity.getSpawnCategory().name().toLowerCase(Locale.ROOT)))
                .forEach(Entity::remove);
    }
}
