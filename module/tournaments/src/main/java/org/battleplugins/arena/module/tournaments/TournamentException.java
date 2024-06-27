package org.battleplugins.arena.module.tournaments;

import org.battleplugins.arena.messages.Message;

public class TournamentException extends IllegalArgumentException {
    private final Message errorMessage;

    public TournamentException(Message errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Message getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
