package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.config.PotionEffectParser;
import org.battleplugins.arena.config.SingularValueParser;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

public class GiveEffectsAction extends EventAction {
    private static final String EFFECTS_KEY = "effects";

    public GiveEffectsAction(Map<String, String> params) {
        super(params, EFFECTS_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
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
            String effectContents = argument.value();

            try {
                PotionEffect effect = PotionEffectParser.deserializeSingular(effectContents);
                arenaPlayer.getPlayer().addPotionEffect(effect);
            } catch (ParseException e) {
                ParseException.handle(e
                        .context("Action", "GiveEffectsAction")
                        .context("Arena", arenaPlayer.getArena().getName())
                        .context("Provided value", effectContents)
                        .cause(ParseException.Cause.INVALID_VALUE)
                        .userError()
                );
            }
        }
    }
}
