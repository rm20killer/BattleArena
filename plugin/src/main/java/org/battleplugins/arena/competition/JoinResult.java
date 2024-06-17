package org.battleplugins.arena.competition;

import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.jetbrains.annotations.Nullable;

public class JoinResult {
    public static final JoinResult SUCCESS = new JoinResult(true, null);
    public static final JoinResult ARENA_FULL = new JoinResult(false, Messages.ARENA_FULL);
    public static final JoinResult NOT_JOINABLE = new JoinResult(false, Messages.ARENA_NOT_JOINABLE);
    public static final JoinResult NOT_SPECTATABLE = new JoinResult(false, Messages.ARENA_NOT_SPECTATABLE);

    private final boolean canJoin;
    private final Message message;

    public JoinResult(boolean canJoin, Message message) {
        this.canJoin = canJoin;
        this.message = message;
    }

    public boolean canJoin() {
        return this.canJoin;
    }

    @Nullable
    public Message getMessage() {
        return this.message;
    }
}
