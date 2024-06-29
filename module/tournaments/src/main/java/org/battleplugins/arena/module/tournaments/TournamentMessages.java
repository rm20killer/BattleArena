package org.battleplugins.arena.module.tournaments;

import org.battleplugins.arena.messages.Message;

import static org.battleplugins.arena.messages.Messages.error;
import static org.battleplugins.arena.messages.Messages.info;
import static org.battleplugins.arena.messages.Messages.success;

public final class TournamentMessages {
    public static final Message TOURNAMENT_CREATED = success("tournament-created", "The tournament has been created for <secondary>{}</secondary>!");
    public static final Message TOURNAMENT_STARTED = success("tournament-started", "The tournament has started for <secondary>{}</secondary>!");
    public static final Message TOURNAMENT_ENDED = success("tournament-ended", "The tournament has ended for <secondary>{}</secondary>!");
    public static final Message TOURNAMENT_JOINED = info("tournament-joined", "You have joined the tournament for <secondary>{}</secondary>!");
    public static final Message TOURNAMENT_LEFT = info("tournament-left", "You have left the tournament for <secondary>{}</secondary>!");
    public static final Message TOURNAMENT_ALREADY_STARTED = error("tournament-already-started", "The tournament has already started!");
    public static final Message TOURNAMENT_NOT_FOUND = error("tournament-not-found", "The tournament could not be found for this arena!");
    public static final Message TOURNAMENT_NO_ACTIVE_TOURNAMENTS = error("tournament-no-active-tournaments", "There are no active tournaments!");
    public static final Message TOURNAMENT_ALREADY_EXISTS = error("tournament-already-exists", "A tournament already exists for this arena!");
    public static final Message TOURNAMENT_ALREADY_JOINED = error("tournament-already-joined", "You have already joined the tournament!");
    public static final Message TOURNAMENT_NOT_IN_TOURNAMENT = error("tournament-not-in-tournament", "You are not in a tournament!");
    public static final Message TOURNAMENT_IN_OTHER_TOURNAMENT = error("tournament-in-other-tournament", "You are already in another tournament!");
    public static final Message TOURNAMENT_NOT_ENOUGH_PLAYERS = error("tournament-not-enough-players", "Not enough players to start the tournament! You need at least {} players to start this tournament.");
    public static final Message TOURNAMENT_NOT_ENOUGH_TEAMS = error("tournament-not-enough-teams", "Not enough teams to start the tournament!");
    public static final Message TOURNAMENT_TEAM_SIZE = error("tournament-team-size", "Each team must be able to fit one or more players in order for a tournament to commence.");
    public static final Message TOURNAMENT_TEAM_AMOUNT = error("tournament-team-amount", "The maximum team amount must be set to 2 in order for a tournament to commence.");
    public static final Message TOURNAMENT_ARENA_NOT_EMPTY = error("tournament-arena-not-empty", "All arenas of this type must be empty in order for a tournament to commence.");
    public static final Message TOURNAMENT_NOT_ENOUGH_ARENAS = error("tournament-not-enough-arenas", "There are not enough arenas to host the tournament!");
    public static final Message TOURNAMENT_CANNOT_JOIN_ARENA = error("tournament-cannot-join-arena", "This arena is currently not joinable due to an ongoing tournament!");
    public static final Message TOURNAMENT_CANNOT_JOIN_ARENA_IN_TOURNAMENT = error("tournament-cannot-join-arena-in-tournament", "You cannot join an arena while in a tournament!");
    public static final Message TOURNAMENT_CANNOT_JOIN_TOURNAMENT_IN_ARENA = error("tournament-cannot-join-tournament-in-arena", "You cannot join a tournament while in an arena!");
    public static final Message TOURNAMENT_BEGINNING_BROADCAST = info("tournament-beginning-broadcast", """
                
                A tournament for <secondary>{}</secondary> is beginning!
                
                <primary>Join the tournament by using <secondary>/tournament join {}</secondary>!</primary>
                """
    );
    public static final Message TOURNAMENT_FIRST_ROUND = info("tournament-first-round", """
                
                The tournament has started! Good luck to all participants!
                
                <secondary>Once all players have finished their matches, the next round will begin.</secondary>
                """
    );
    public static final Message TOURNAMENT_WON_ROUND = success("tournament-won-round", """
                
                Congratulations! You have won the current round in the tournament!
                
                <secondary>Once all remaining players have finished their matches, the next round will begin.</secondary>
                """
    );
    public static final Message TOURNAMENT_LOST_ROUND = error("tournament-lost-round", """
                
                You have lost the round in this tournament and have been eliminated.
                
                <secondary>Once all remaining players have finished their matches, the next round will begin.</secondary>
                """
    );
    public static final Message TOURNAMENT_DREW_ROUND = info("tournament-drew-round", """
                
                This round has ended in a draw! You and your opponent will be playing another match.
                
                <secondary>Once all remaining players have finished their matches, the next round will begin.</secondary>
                """
    );
    public static final Message TOURNAMENT_SKIPPED_ROUND = info("tournament-skipped-round", """
                
                You have been skipped for this round.
                
                <secondary>This is due to an uneven amount of players in the tournament.</secondary>
                <secondary>Once all remaining players have finished their matches, the next round will begin.</secondary>
                """
    );
    public static final Message NEXT_ROUND_STARTING = info("tournament-next-round-starting", """
                
                The next round of the tournament is starting!
                
                <secondary>Good luck to all participants!</secondary>
                """
    );
    public static final Message NEXT_ROUND_STARTING_IN = info("tournament-next-round-starting-in", "The next round of the tournament will start in <secondary>{}</secondary>!");
    public static final Message TOURNAMENT_COMPLETED = success("tournament-completed", """
                
                The tournament has been completed! {}.
                
                <secondary>Thank you to all participants for playing!</secondary>
                """
    );
    public static final Message TOURNAMENT_CONGRATULATIONS_TO_WINNERS = success("tournament-congratulations-to-winners", "Congratulations to the winners: <secondary>{}</secondary>");
    public static final Message TOURNAMENT_DRAW = info("tournament-draw", "The tournament has ended in a draw!");
}
