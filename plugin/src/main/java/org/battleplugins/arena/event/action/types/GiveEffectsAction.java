package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.config.SingularValueParser;
import org.battleplugins.arena.event.action.EventAction;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class GiveEffectsAction extends EventAction {
    private static final String EFFECTS_KEY = "effects";

    public GiveEffectsAction(Map<String, String> params) {
        super(params, EFFECTS_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer) {
        SingularValueParser.ArgumentBuffer buffer;
        try {
            buffer = SingularValueParser.parseUnnamed(this.get(EFFECTS_KEY), SingularValueParser.BraceStyle.SQUARE, ',');
        } catch (ParseException e) {
            ParseException.handle(e
                    .context("Action", "GiveEffectsAction")
                    .context("Arena", arenaPlayer.getArena().getName())
                    .context("Provided value", this.get(EFFECTS_KEY))
                    .cause(ParseException.Cause.INVALID_VALUE)
                    .userError()
            );
            return;
        }

        if (!buffer.hasNext()) {
            return;
        }

        while (buffer.hasNext()) {
            SingularValueParser.Argument argument = buffer.pop();
            String effect = argument.value();

            String[] effectSplit = effect.split(" ");
            PotionEffectType effectType = PotionEffectType.getByName(effectSplit[0]);
            if (effectType == null) {
                throw new IllegalArgumentException("Invalid potion effect " + effectSplit[0]);
            }

            int duration = Integer.parseInt(effectSplit[1]) * 20;
            int amplifier = Integer.parseInt(effectSplit[2]) - 1;

            arenaPlayer.getPlayer().addPotionEffect(effectType.createEffect(duration, amplifier));
        }
    }
}
