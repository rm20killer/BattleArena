package org.battleplugins.arena.module.duels;

import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;

public final class DuelsMessages {
    public static final Message CANNOT_DUEL_SELF = Messages.error("duel-cannot-duel-self", "You cannot duel yourself!");
    public static final Message DUEL_REQUEST_SENT = Messages.info("duel-request-sent", "Duel request sent to <secondary>{}</secondary>!");
    public static final Message DUEL_REQUEST_RECEIVED = Messages.info("duel-request-received", "You have received a duel request from <secondary>{}</secondary>! Type <secondary>/{} duel accept {}</secondary> to accept or <secondary>/{} duel deny {}</secondary> to deny.");
    public static final Message DUEL_REQUEST_ALREADY_SENT = Messages.error("duel-request-already-sent", "You have already have an outgoing duel request! Type <secondary>/{} duel cancel</secondary> to cancel.");
    public static final Message NO_DUEL_REQUESTS = Messages.error("duel-no-requests", "You have no duel requests!");
    public static final Message USER_DID_NOT_REQUEST = Messages.error("duel-user-did-not-request", "<secondary>{}</secondary> did not request a duel with you! You can only accept requests from the user who sent it.");
    public static final Message DUEL_REQUEST_DENIED = Messages.info("duel-request-denied", "You have denied the duel request from <secondary>{}</secondary>!");
    public static final Message DENIED_DUEL_REQUEST = Messages.error("duel-denied-request", "<secondary>{}</secondary> has denied your duel request!");
    public static final Message DUEL_REQUEST_CANCELLED = Messages.info("duel-request-cancelled", "You have cancelled your duel request to <secondary>{}</secondary>!");
    public static final Message CANCELLED_DUEL_REQUEST = Messages.error("duel-cancelled-request", "<secondary>{}</secondary> has cancelled their duel request!");
    public static final Message DUEL_REQUESTED_CANCELLED_QUIT = Messages.info("duel-request-cancelled-quit", "Your duel request from <secondary>{}</secondary> has been cancelled as they have left the server.");
    public static final Message DUEL_REQUEST_ACCEPTED = Messages.info("duel-request-accepted", "You have accepted the duel request from <secondary>{}</secondary>! The duel will commence in <secondary>5 seconds</secondary>!");
    public static final Message ACCEPTED_DUEL_REQUEST = Messages.info("duel-accepted-request", "<secondary>{}</secondary> has accepted your duel request! The duel will commence in <secondary>5 seconds</secondary>!");
    public static final Message PENDING_DUEL_REQUEST = Messages.error("duel-pending-request", "You have a pending outgoing duel request! Type <secondary>/arena duel cancel</secondary> to cancel.");
}
