package org.battleplugins.arena.competition.phase.phases;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.phase.LiveCompetitionPhase;
import org.battleplugins.arena.messages.Messages;
import org.battleplugins.arena.options.Lives;
import org.battleplugins.arena.stat.ArenaStats;

public class IngamePhase<T extends LiveCompetition<T>> extends LiveCompetitionPhase<T> {

    @Override
    public void onStart() {
        Lives lives = this.getCompetition().getArena().getLives();
        if (lives != null && lives.isEnabled()) {
            for (ArenaPlayer player : this.getCompetition().getPlayers()) {
                player.setStat(ArenaStats.LIVES, lives.getLives());
            }
        }

        for (ArenaPlayer player : this.getCompetition().getPlayers()) {
            Messages.FIGHT.send(player.getPlayer());
        }
    }

    @Override
    public void onComplete() {

    }
}
