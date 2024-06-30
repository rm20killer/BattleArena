package org.battleplugins.arena.competition;

import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the result of a player joining an arena.
 *
 * @param canJoin whether the player can join the arena
 * @param message the message to send to the player if they cannot join
 */
public record JoinResult(boolean canJoin, @Nullable Message message) {
    public static final JoinResult SUCCESS = new JoinResult(true, null);
    public static final JoinResult ARENA_FULL = new JoinResult(false, Messages.ARENA_FULL);
    public static final JoinResult NOT_JOINABLE = new JoinResult(false, Messages.ARENA_NOT_JOINABLE);
    public static final JoinResult NOT_SPECTATABLE = new JoinResult(false, Messages.ARENA_NOT_SPECTATABLE);
}
