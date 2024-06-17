package org.battleplugins.arena.module.teamheads;

import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.module.ArenaModule;

/**
 * A module that adds a custom action which adds a wool block
 * to a player's head based on their team color.
 */
@ArenaModule(id = TeamHeads.ID, name = "Team Heads", description = "Adds the color of a player's team to their head.", authors = "BattlePlugins")
public class TeamHeads {
    public static final String ID = "team-heads";

    private static final EventActionType<TeamHeadsAction> TEAM_HEADS = EventActionType.create("team-heads", TeamHeadsAction.class, TeamHeadsAction::new);
}
