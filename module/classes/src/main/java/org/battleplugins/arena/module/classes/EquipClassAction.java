package org.battleplugins.arena.module.classes;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.options.types.BooleanArenaOption;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;
import java.util.Optional;

public class EquipClassAction extends EventAction {
    private static final String CLASS_KEY = "class";
    private static final String CLEAR_INVENTORY_KEY = "clear-inventory";
    private static final String IGNORE_PLAYER_SELECTION_KEY = "ignore-player-selection";

    public EquipClassAction(Map<String, String> params) {
        super(params, CLASS_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (!arenaPlayer.getArena().isModuleEnabled(Classes.ID)) {
            return;
        }

        Arena arena = arenaPlayer.getArena();
        Optional<Classes> moduleOpt = arena.getPlugin()
                .<Classes>module(Classes.ID)
                .map(module -> module.initializer(Classes.class));

        // No class module or classes are disabled
        if (moduleOpt.isEmpty() || !arena.isModuleEnabled(Classes.ID)) {
            return;
        }

        Classes module = moduleOpt.get();
        String className = this.get(CLASS_KEY);

        ArenaClass arenaClass = module.getClass(className);
        if (arenaClass == null) {
            return;
        }

        boolean clearInventory = Boolean.parseBoolean(this.getOrDefault(CLEAR_INVENTORY_KEY, "true"));
        if (clearInventory) {
            arenaPlayer.getPlayer().getInventory().clear();
        }

        boolean equipOnlySelects = arenaPlayer.getCompetition().option(Classes.CLASS_EQUIP_ONLY_SELECTS_OPTION)
                .map(BooleanArenaOption::isEnabled)
                .orElse(false);

        boolean ignorePlayerSelection = Boolean.parseBoolean(this.getOrDefault(IGNORE_PLAYER_SELECTION_KEY, "false"));

        // If equip only selects, by this point, the player may have
        // selected a class, which means we want to equip it here
        if (!ignorePlayerSelection && equipOnlySelects) {
            ArenaClass selectedClass = arenaPlayer.getMetadata(ArenaClass.class);
            if (selectedClass != null) {
                selectedClass.equip(arenaPlayer);
                return;
            }
        }

        // Otherwise, equip the class we have here
        arenaClass.equip(arenaPlayer);
    }
}
