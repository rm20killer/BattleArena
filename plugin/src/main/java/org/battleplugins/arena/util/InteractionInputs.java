package org.battleplugins.arena.util;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InteractionInputs {

    public static abstract class ChatInput {

        /**
         * Constructs a new ChatInput instance
         *
         * @param player the player to receive the chat input from
         */
        public ChatInput(Player player, Message invalidInput) {
            Listener listener = new Listener() {

                @EventHandler
                public void onChat(AsyncChatEvent event) {
                    if (!player.equals(event.getPlayer())) {
                        return;
                    }

                    event.setCancelled(true);
                    String message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
                    if (!isValidChatInput(message)) {
                        if (invalidInput != null) {
                            invalidInput.send(player);
                        }

                        return;
                    }

                    // Run task synchronously since chat is async
                    Bukkit.getScheduler().runTask(BattleArena.getInstance(), () -> {
                        onChatInput(message);
                    });

                    HandlerList.unregisterAll(this);
                }
            };

            Bukkit.getPluginManager().registerEvents(listener, BattleArena.getInstance());
        }

        /**
         * Runs when the player enters text in chat
         *
         * @param input the text the player inputted
         */
        public abstract void onChatInput(String input);

        /**
         * Checks if the input is valid
         *
         * @param input the input to check
         * @return true if the input is valid, false otherwise
         */
        public boolean isValidChatInput(String input) {
            return true;
        }
    }

    public static abstract class InventoryInput {

        /**
         * Constructs a new InventoryInput instance
         *
         * @param player the player to receive the inventory input from
         */
        public InventoryInput(Player player) {
            Listener listener = new Listener() {

                @EventHandler
                public void onInventoryClick(InventoryClickEvent event) {
                    if (!player.equals(event.getWhoClicked())) {
                        return;
                    }

                    if (!player.getInventory().equals(event.getClickedInventory())) {
                        Messages.INVALID_INVENTORY_CANCELLING.send(player);
                        HandlerList.unregisterAll(this);
                        return;
                    }

                    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                        return;
                    }

                    ItemStack currentItem = event.getCurrentItem().clone();

                    // Need to run a tick later so Bukkit can handle the event cancellation
                    Bukkit.getScheduler().runTaskLater(BattleArena.getInstance(), () -> {
                        onInventoryInteract(currentItem);
                    }, 1);

                    // This is needed to prevent the item from being picked up in
                    // creative. Seems overkill but Bukkit(TM)
                    event.setCancelled(true);
                    event.getView().setCursor(null);
                    player.updateInventory();

                    HandlerList.unregisterAll(this);
                }
            };

            Bukkit.getPluginManager().registerEvents(listener, BattleArena.getInstance());
        }

        /**
         * Runs when the player interacts with an item in
         * their inventory
         *
         * @param item the item the player interacted with
         */
        public abstract void onInventoryInteract(ItemStack item);
    }

    public static abstract class PositionInput {
        private static final Map<UUID, Long> LAST_INPUT = new HashMap<>();

        /**
         * Constructs a new PositionInput instance
         *
         * @param player the player to receive the position input from
         */
        public PositionInput(Player player) {
            Listener listener = new Listener() {

                @EventHandler
                public void onInteract(PlayerInteractEvent event) {
                    if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }

                    if (event.getInteractionPoint() == null) {
                        return;
                    }

                    if (LAST_INPUT.containsKey(player.getUniqueId()) && System.currentTimeMillis() - LAST_INPUT.get(player.getUniqueId()) < 1000) {
                        return;
                    }

                    LAST_INPUT.put(player.getUniqueId(), System.currentTimeMillis());

                    // Need to run a tick later so Bukkit can handle the event cancellation
                    Bukkit.getScheduler().runTaskLater(BattleArena.getInstance(), () -> {
                        onPositionInteract(event.getInteractionPoint());
                    }, 1);

                    event.setCancelled(true);

                    HandlerList.unregisterAll(this);
                }
            };

            Bukkit.getPluginManager().registerEvents(listener, BattleArena.getInstance());
        }

        /**
         * Runs when the player interacts at a position in the world.
         *
         * @param position the position the player interacted at
         */
        public abstract void onPositionInteract(Location position);
    }
}