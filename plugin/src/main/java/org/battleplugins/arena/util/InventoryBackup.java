package org.battleplugins.arena.util;

import org.battleplugins.arena.BattleArena;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class InventoryBackup {
    private static final String INVENTORY_TYPE = "inventory";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    private final long timestamp;
    private final UUID uuid;
    private final ItemStack[] items;

    private Path path;

    public InventoryBackup(UUID uuid, ItemStack[] items) {
        this(System.currentTimeMillis(), uuid, items);
    }

    public InventoryBackup(long timestamp, UUID uuid, ItemStack[] items) {
        this.timestamp = timestamp;
        this.uuid = uuid;
        this.items = items;
    }

    public void restore(Player player) {
        // Clear the player's inventory
        player.getInventory().clear();

        // Set the player's inventory to the backup
        player.getInventory().setContents(this.items);
    }

    public ItemStack[] getItems() {
        return this.items;
    }

    public Instant getTimestamp() {
        return Instant.ofEpochMilli(this.timestamp);
    }

    public String getFormattedDate() {
        return DATE_FORMAT.format(this.timestamp);
    }

    private void save() {
        // Save the inventory backup to the specified path
        Path path = BattleArena.getInstance().getBackupPath(INVENTORY_TYPE)
                .resolve(this.uuid.toString()).resolve(DATE_FORMAT.format(System.currentTimeMillis()) + ".dat");

        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             DataOutputStream stream = new DataOutputStream(byteStream)) {
            stream.writeLong(this.timestamp);
            stream.writeInt(this.items.length);
            for (ItemStack item : this.items) {
                if (item == null) {
                    stream.writeBoolean(false);
                    continue;
                }

                stream.writeBoolean(true);

                byte[] itemBytes = item.serializeAsBytes();
                stream.writeInt(itemBytes.length);
                stream.write(itemBytes);
            }

            byte[] data = byteStream.toByteArray();

            Path parent = path.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            Files.write(path, data);
        } catch (IOException e) {
            BattleArena.getInstance().error("Failed to save inventory backup for " + this.uuid, e);
        }
    }

    public static void save(InventoryBackup backup) {
        List<InventoryBackup> backups = load(backup.uuid, InventoryBackup::loadShell);
        if (backups.size() >= BattleArena.getInstance().getMainConfig().getMaxBackups()) {
            try {
                Files.delete(backups.get(backups.size() - 1).path);
            } catch (IOException e) {
                BattleArena.getInstance().error("Failed to delete oldest inventory backup for " + backup.uuid, e);
                return;
            }
        }

        backup.save();
    }

    public static List<InventoryBackup> load(UUID uuid) {
        return load(uuid, InventoryBackup::load);
    }

    @Nullable
    static InventoryBackup load(UUID uuid, Path path) {
        if (Files.notExists(path)) {
            return null;
        }

        try {
            byte[] data = Files.readAllBytes(path);
            try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data))) {
                long timestamp = stream.readLong();
                int length = stream.readInt();
                ItemStack[] items = new ItemStack[length];
                for (int i = 0; i < length; i++) {
                    boolean itemPresent = stream.readBoolean();
                    if (itemPresent) {
                        int itemLength = stream.readInt();
                        byte[] itemBytes = new byte[itemLength];

                        stream.readFully(itemBytes);
                        items[i] = ItemStack.deserializeBytes(itemBytes);
                    }
                }

                InventoryBackup backup = new InventoryBackup(timestamp, uuid, items);
                backup.path = path;
                return backup;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load inventory backup for " + uuid + "! Corrupted file?", e);
        }
    }

    @Nullable
    private static InventoryBackup loadShell(UUID uuid, Path path) {
        if (Files.notExists(path)) {
            return null;
        }

        try {
            byte[] data = Files.readAllBytes(path);
            try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data))) {
                long timestamp = stream.readLong();

                InventoryBackup backup = new InventoryBackup(timestamp, uuid, null) {
                    private ItemStack[] items;

                    @Override
                    public ItemStack[] getItems() {
                        if (items == null) {
                            items = load(uuid, path).getItems();
                        }

                        return items;
                    }
                };

                backup.path = path;
                return backup;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load inventory backup for " + uuid + "! Corrupted file?", e);
        }
    }

    private static List<InventoryBackup> load(UUID uuid, BiFunction<UUID, Path, InventoryBackup> loader) {
        // Load all inventory backups for the specified UUID
        Path path = BattleArena.getInstance().getBackupPath(INVENTORY_TYPE).resolve(uuid.toString());
        try {
            if (Files.notExists(path)) {
                return List.of();
            }

            try (Stream<Path> stream = Files.list(path)) {
                // Sort based on most recent backup
                return stream.map(p -> loader.apply(uuid, p))
                        .sorted((a, b) -> Long.compare(b.timestamp, a.timestamp))
                        .toList();
            }
        } catch (IOException e) {
            BattleArena.getInstance().error("Failed to load inventory backups for " + uuid, e);
        }

        return List.of();
    }
}
