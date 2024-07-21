package org.battleplugins.arena.module.duels;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.command.ArenaCommand;
import org.battleplugins.arena.command.SubCommandExecutor;
import org.battleplugins.arena.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

public class DuelsExecutor implements SubCommandExecutor {
    private final Duels module;
    private final Arena arena;
    private final String parentCommand;

    public DuelsExecutor(Duels module, Arena arena) {
        this.module = module;
        this.arena = arena;
        this.parentCommand = arena.getName().toLowerCase(Locale.ROOT);
    }

    @ArenaCommand(commands = "duel", description = "Duel another player.", permissionNode = "duel")
    public void duel(Player player, Player target) {
        if (player.equals(target)) {
            DuelsMessages.CANNOT_DUEL_SELF.send(player);
            return;
        }

        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
        if (arenaPlayer != null) {
            Messages.ALREADY_IN_ARENA.send(player);
            return;
        }

        ArenaPlayer targetPlayer = ArenaPlayer.getArenaPlayer(target);
        if (targetPlayer != null) {
            Messages.ALREADY_IN_ARENA.send(player);
            return;
        }

        if (this.module.getDuelRequests().containsKey(player.getUniqueId())) {
            DuelsMessages.DUEL_REQUEST_ALREADY_SENT.send(player, this.parentCommand);
            return;
        }

        DuelsMessages.DUEL_REQUEST_SENT.send(player, target.getName());
        DuelsMessages.DUEL_REQUEST_RECEIVED.send(
                target,
                player.getName(),
                this.parentCommand,
                player.getName(),
                this.parentCommand,
                player.getName()
        );

        this.module.addDuelRequest(player.getUniqueId(), target.getUniqueId());
    }

    @ArenaCommand(commands = "duel", subCommands = "accept", description = "Accept a duel request.", permissionNode = "duel.accept")
    public void acceptDuel(Player player, Player target) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
        if (arenaPlayer != null) {
            Messages.ALREADY_IN_ARENA.send(player);
            return;
        }

        UUID requestedId = this.module.getDuelRequests().get(target.getUniqueId());
        if (requestedId == null) {
            DuelsMessages.NO_DUEL_REQUESTS.send(player);
            return;
        }

        if (!requestedId.equals(player.getUniqueId())) {
            DuelsMessages.USER_DID_NOT_REQUEST.send(player, target.getName());
            return;
        }

        DuelsMessages.DUEL_REQUEST_ACCEPTED.send(player, target.getName());
        DuelsMessages.ACCEPTED_DUEL_REQUEST.send(target, player.getName());

        this.module.removeDuelRequest(target.getUniqueId());

        Bukkit.getScheduler().runTaskLater(BattleArena.getInstance(), () -> {
            this.module.acceptDuel(this.arena, player, target);
        }, 100);
    }
    
    @ArenaCommand(commands = "duel", subCommands = "deny", description = "Deny a duel request.", permissionNode = "duel.deny")
    public void denyDuel(Player player, Player target) {
        UUID requestedId = this.module.getDuelRequests().get(target.getUniqueId());
        if (requestedId == null) {
            DuelsMessages.NO_DUEL_REQUESTS.send(player);
            return;
        }

        if (!requestedId.equals(player.getUniqueId())) {
            DuelsMessages.USER_DID_NOT_REQUEST.send(player, target.getName());
            return;
        }

        DuelsMessages.DUEL_REQUEST_DENIED.send(player, target.getName());
        DuelsMessages.DENIED_DUEL_REQUEST.send(target, player.getName());
        
        this.module.removeDuelRequest(target.getUniqueId());
    }
    
    @ArenaCommand(commands = "duel", subCommands = "cancel", description = "Cancel a duel request.", permissionNode = "duel.cancel")
    public void cancelDuel(Player player) {
        if (!this.module.getDuelRequests().containsKey(player.getUniqueId())) {
            DuelsMessages.NO_DUEL_REQUESTS.send(player);
            return;
        }

        UUID targetId = this.module.getDuelRequests().get(player.getUniqueId());
        Player target = Bukkit.getServer().getPlayer(targetId);
        if (target == null) {
            // Shouldn't get here but just incase
            this.module.removeDuelRequest(player.getUniqueId());
            
            DuelsMessages.NO_DUEL_REQUESTS.send(player);
            return;
        }
        
        DuelsMessages.DUEL_REQUEST_CANCELLED.send(player, target.getName());
        DuelsMessages.CANCELLED_DUEL_REQUEST.send(target, player.getName());

        this.module.removeDuelRequest(player.getUniqueId());
    }
}
