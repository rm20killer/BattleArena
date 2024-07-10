package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

public class PlaySoundAction extends EventAction {
    private static final String SOUND_KEY = "sound";
    private static final String PITCH_KEY = "pitch";
    private static final String VOLUME_KEY = "volume";

    public PlaySoundAction(Map<String, String> params) {
        super(params, SOUND_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String sound = this.get(SOUND_KEY);
        float pitch = Float.parseFloat(this.getOrDefault(PITCH_KEY, "1"));
        float volume = Float.parseFloat(this.getOrDefault(VOLUME_KEY, "1"));

        arenaPlayer.getPlayer().playSound(arenaPlayer.getPlayer().getLocation(), sound, volume, pitch);
    }
}
