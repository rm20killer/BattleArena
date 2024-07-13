package org.battleplugins.arena.module.classes;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;
import org.battleplugins.arena.config.PostProcessable;
import org.battleplugins.arena.config.Scoped;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/classes")
public class ArenaClass implements PostProcessable {

    @Scoped
    private ClassesConfig classes;

    @ArenaOption(name = "name", description = "The name of the class.", required = true)
    private String name;

    @ArenaOption(name = "items", description = "The items the class will have.")
    private List<ItemStack> items;

    @ArenaOption(name = "equipment", description = "The equipment the class will have.")
    private Map<String, ItemStack> equipmentConfig;

    private final Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();

    @Override
    public void postProcess() {
        // Process armor
        if (this.equipmentConfig == null) {
            return;
        }

        for (Map.Entry<String, ItemStack> entry : this.equipmentConfig.entrySet()) {
            EquipmentSlot slot = slotFromString(entry.getKey().toUpperCase());
            if (slot == null) {
                this.classes.plugin.warn("Invalid equipment slot when loading class {}: {}", this.name, entry.getKey());
                continue;
            }

            this.equipment.put(slot, entry.getValue());
        }
    }

    public String getName() {
        return this.name;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return this.equipment;
    }

    public void equip(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();

        // Equip items
        for (ItemStack item : this.items) {
            player.getInventory().addItem(item);
        }

        // Equip armor
        for (Map.Entry<EquipmentSlot, ItemStack> entry : this.equipment.entrySet()) {
            player.getEquipment().setItem(entry.getKey(), entry.getValue());
        }
    }

    private static EquipmentSlot slotFromString(String slot) {
        return switch (slot.toLowerCase(Locale.ROOT)) {
            case "head", "helmet" -> EquipmentSlot.HEAD;
            case "chest", "chestplate" -> EquipmentSlot.CHEST;
            case "legs", "leggings" -> EquipmentSlot.LEGS;
            case "feet", "boots" -> EquipmentSlot.FEET;
            case "offhand", "off_hand" -> EquipmentSlot.OFF_HAND;
            default -> null;
        };
    }
}
