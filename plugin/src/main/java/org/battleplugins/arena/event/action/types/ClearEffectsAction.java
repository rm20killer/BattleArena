package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public class ClearEffectsAction extends EventAction {

    public ClearEffectsAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        for (PotionEffect effect : List.copyOf(arenaPlayer.getPlayer().getActivePotionEffects())) {
            arenaPlayer.getPlayer().removePotionEffect(effect.getType());
        }
    }
}
