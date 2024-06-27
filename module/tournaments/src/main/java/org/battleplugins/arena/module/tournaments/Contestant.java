package org.battleplugins.arena.module.tournaments;

import org.battleplugins.arena.BattleArena;
import org.bukkit.entity.Player;

import java.util.Set;

public class Contestant {
    private final Set<Player> players;

    private int wins;
    private int losses;
    private int byes;

    public Contestant(Set<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public void clearPlayers() {
        this.players.clear();
    }

    public Set<Player> getPlayers() {
        return Set.copyOf(this.players);
    }

    public int getWins() {
        return this.wins;
    }

    public void addWin() {
        this.wins++;
    }

    public int getLosses() {
        return this.losses;
    }

    public void addLoss() {
        this.losses++;
    }

    public int getByes() {
        return this.byes;
    }

    public void addBye() {
        this.byes++;
    }

    public boolean isDone() {
        for (Player player : this.players) {
            if (BattleArena.getInstance().isInArena(player)) {
                return false;
            }
        }

        return true;
    }
}
