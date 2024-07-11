package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.config.ItemStackParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GiveItemAction extends EventAction {
    private static final String ITEM_KEY = "item";
    private static final String SLOT_KEY = "slot";

    public GiveItemAction(Map<String, String> params) {
        super(params, ITEM_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String item = this.get(ITEM_KEY);
        try {
            ItemStack itemStack = ItemStackParser.deserializeSingular(item);
            int slot = Integer.parseInt(this.getOrDefault(SLOT_KEY, "-1"));
            if (slot == -1) {
                arenaPlayer.getPlayer().getInventory().addItem(itemStack);
            } else {
                arenaPlayer.getPlayer().getInventory().setItem(slot, itemStack);
            }
        } catch (ParseException e) {
            ParseException.handle(e
                    .context("Action", "GiveItemAction")
                    .context("Arena", arenaPlayer.getArena().getName())
                    .context("Provided value", this.get(item))
                    .cause(ParseException.Cause.INVALID_VALUE)
                    .userError()
            );
        }
    }
}
