package org.battleplugins.arena.competition;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.util.InventoryBackup;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents the storage of a player for data which may need to
 * be restored after the end of a competition.
 */
public class PlayerStorage {
    private final ArenaPlayer player;
    
    private ItemStack[] inventory;
    private GameMode gameMode;
    private final Map<Attribute, Double> attributes = new HashMap<>();

    private double health;
    private int hunger;

    private int totalExp;
    private float exp;
    private int expLevels;

    private float walkSpeed;
    private float flySpeed;

    private boolean flight;
    private boolean allowFlight;

    private final Collection<PotionEffect> effects = new ArrayList<>();
    
    private Location lastLocation;
    
    private boolean stored;
    
    public PlayerStorage(ArenaPlayer player) {
        this.player = player;
    }

    /**
     * Stores the player's data from on the given {@link Type types}.
     *
     * @param toStore the types to store
     */
    public void store(Set<Type> toStore, boolean clearState) {
        if (this.stored) {
            return;
        }

        for (Type type : toStore) {
            type.store(this);
        }

        this.stored = true;
        if (clearState) {
            this.clearState(toStore);
        }
    }

    private void storeAll() {
        this.storeInventory();
        this.storeGameMode();
        this.storeHealth();
        this.storeAttributes();
        this.storeExperience();
        this.storeFlight();
        this.storeEffects();
        this.storeLocation();
    }

    private void storeInventory() {
        this.inventory = new ItemStack[this.player.getPlayer().getInventory().getSize()];
        for (int i = 0; i < this.inventory.length; i++) {
            ItemStack item = this.player.getPlayer().getInventory().getItem(i);
            if (item == null) {
                continue;
            }

            this.inventory[i] = item.clone();
        }

        if (BattleArena.getInstance().getMainConfig().isBackupInventories()) {
            InventoryBackup.save(new InventoryBackup(this.player.getPlayer().getUniqueId(), this.inventory.clone()));
        }
    }

    private void storeGameMode() {
        this.gameMode = this.player.getPlayer().getGameMode();
    }

    private void storeHealth() {
        this.health = this.player.getPlayer().getHealth();
        this.hunger = this.player.getPlayer().getFoodLevel();
    }

    private void storeAttributes() {
        for (Attribute attribute : Attribute.values()) {
            AttributeInstance instance = this.player.getPlayer().getAttribute(attribute);
            if (instance == null) {
                continue;
            }
            
            this.attributes.put(attribute, instance.getBaseValue());
        }

        this.walkSpeed = this.player.getPlayer().getWalkSpeed();
        this.flySpeed = this.player.getPlayer().getFlySpeed();
    }

    private void storeExperience() {
        this.totalExp = this.player.getPlayer().getTotalExperience();
        this.exp = this.player.getPlayer().getExp();
        this.expLevels = this.player.getPlayer().getLevel();
    }

    private void storeFlight() {
        this.flight = this.player.getPlayer().isFlying();
        this.allowFlight = this.player.getPlayer().getAllowFlight();
    }

    private void storeEffects() {
        this.effects.addAll(this.player.getPlayer().getActivePotionEffects());
    }

    private void storeLocation() {
        this.lastLocation = this.player.getPlayer().getLocation().clone();
    }

    /**
     * Restores the player's data from on the given {@link Type types}.
     *
     * @param toRestore the types to restore
     */
    public void restore(Set<Type> toRestore) {
        if (!this.stored) {
            return;
        }
        
        for (Type type : toRestore) {
            type.restore(this);
        }
        
        // Reset everything we have in this class
        this.inventory = null;
        this.attributes.clear();
        this.health = 0;
        this.hunger = 0;
        this.totalExp = 0;
        this.exp = 0;
        this.expLevels = 0;
        this.effects.clear();
        this.lastLocation = null;
        
        this.stored = false;
    }

    private void restoreAll() {
        this.restoreInventory();
        this.restoreGameMode();
        this.restoreHealth();
        this.restoreAttributes();
        this.restoreExperience();
        this.restoreFlight();
        this.restoreEffects();
        this.restoreLocation();
    }

    private void restoreInventory() {
        this.player.getPlayer().getInventory().setContents(this.inventory);
    }

    private void restoreGameMode() {
        this.player.getPlayer().setGameMode(this.gameMode);
    }

    private void restoreHealth() {
        this.player.getPlayer().setHealth(this.health);
        this.player.getPlayer().setFoodLevel(this.hunger);
    }

    private void restoreAttributes() {
        for (Map.Entry<Attribute, Double> entry : this.attributes.entrySet()) {
            AttributeInstance instance = this.player.getPlayer().getAttribute(entry.getKey());
            if (instance == null) {
                continue;
            }

            instance.setBaseValue(entry.getValue());
        }

        this.player.getPlayer().setWalkSpeed(this.walkSpeed);
        this.player.getPlayer().setFlySpeed(this.flySpeed);
    }

    private void restoreFlight() {
        this.player.getPlayer().setAllowFlight(this.allowFlight);
        this.player.getPlayer().setFlying(this.flight);
    }

    private void restoreExperience() {
        this.player.getPlayer().setTotalExperience(this.totalExp);
        this.player.getPlayer().setExp(this.exp);
        this.player.getPlayer().setLevel(this.expLevels);
    }

    private void restoreEffects() {
        // Clear all effects from the arena
        for (PotionEffect effect : this.player.getPlayer().getActivePotionEffects()) {
            this.player.getPlayer().removePotionEffect(effect.getType());
        }

        for (PotionEffect effect : this.effects) {
            this.player.getPlayer().addPotionEffect(effect);
        }
    }
    
    private void restoreLocation() {
        this.player.getPlayer().teleport(this.lastLocation);
    }

    /**
     * Returns the last stored location of the player.
     *
     * @return the last stored location of the player
     */
    @Nullable
    public Location getLastLocation() {
        return this.lastLocation;
    }

    private void clearState(Set<Type> toStore) {
        boolean all = toStore.contains(Type.ALL);
        if (all || toStore.contains(Type.INVENTORY)) {
            this.player.getPlayer().getInventory().clear();
        }

        if (all || toStore.contains(Type.GAMEMODE)) {
            this.player.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
        
        if (all || toStore.contains(Type.ATTRIBUTES)) {
            for (Attribute attribute : this.attributes.keySet()) {
                this.player.getPlayer().getAttribute(attribute).setBaseValue(this.player.getPlayer().getAttribute(attribute).getDefaultValue());
            }

            // Because we love consistency in the MC codebase (:
            this.player.getPlayer().setWalkSpeed(0.2f);
            this.player.getPlayer().setFlySpeed(0.1f);
        }

        if (all || toStore.contains(Type.HEALTH)) {
            this.player.getPlayer().setHealth(this.player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            this.player.getPlayer().setFoodLevel(20);
        }
        
        if (all || toStore.contains(Type.EXPERIENCE)) {
            this.player.getPlayer().setTotalExperience(0);
            this.player.getPlayer().setExp(0);
            this.player.getPlayer().setLevel(0);
        }

        if (all || toStore.contains(Type.FLIGHT)) {
            this.player.getPlayer().setAllowFlight(false);
            this.player.getPlayer().setFlying(false);
        }
        
        if (all || toStore.contains(Type.EFFECTS)) {
            for (PotionEffect effect : this.effects) {
                this.player.getPlayer().removePotionEffect(effect.getType());
            }
        }
    }

    /**
     * The different types of data that can be stored/restored.
     */
    public enum Type {
        ALL(PlayerStorage::storeAll, PlayerStorage::restoreAll),
        INVENTORY(PlayerStorage::storeInventory, PlayerStorage::restoreInventory),
        GAMEMODE(PlayerStorage::storeGameMode, PlayerStorage::restoreGameMode),
        HEALTH(PlayerStorage::storeHealth, PlayerStorage::restoreHealth),
        ATTRIBUTES(PlayerStorage::storeAttributes, PlayerStorage::restoreAttributes),
        EXPERIENCE(PlayerStorage::storeExperience, PlayerStorage::restoreExperience),
        FLIGHT(PlayerStorage::storeFlight, PlayerStorage::restoreFlight),
        EFFECTS(PlayerStorage::storeEffects, PlayerStorage::restoreEffects),
        LOCATION(PlayerStorage::storeLocation, PlayerStorage::restoreLocation);
        
        private final Consumer<PlayerStorage> storeFunction;
        private final Consumer<PlayerStorage> restoreFunction;
        
        Type(Consumer<PlayerStorage> storeFunction, Consumer<PlayerStorage> restoreFunction) {
            this.storeFunction = storeFunction;
            this.restoreFunction = restoreFunction;
        }
        
        public void store(PlayerStorage storage) {
            this.storeFunction.accept(storage);
        }
        
        public void restore(PlayerStorage storage) {
            this.restoreFunction.accept(storage);
        }
    }
}
