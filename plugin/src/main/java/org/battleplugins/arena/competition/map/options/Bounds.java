package org.battleplugins.arena.competition.map.options;

import io.papermc.paper.math.Position;
import org.battleplugins.arena.config.ArenaOption;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

/**
 * Represents the bounds of a map.
 */
public class Bounds {
    @ArenaOption(name = "min-x", description = "The minimum X coordinate of the map.", required = true)
    private int minX;
    @ArenaOption(name = "min-y", description = "The minimum Y coordinate of the map.", required = true)
    private int minY;
    @ArenaOption(name = "min-z", description = "The minimum Z coordinate of the map.", required = true)
    private int minZ;

    @ArenaOption(name = "max-x", description = "The maximum X coordinate of the map.", required = true)
    private int maxX;
    @ArenaOption(name = "max-y", description = "The maximum Y coordinate of the map.", required = true)
    private int maxY;
    @ArenaOption(name = "max-z", description = "The maximum Z coordinate of the map.", required = true)
    private int maxZ;

    public Bounds() {
    }

    public Bounds(Position min, Position max) {
        this(min.blockX(), min.blockY(), min.blockZ(), max.blockX(), max.blockY(), max.blockZ());
    }

    public Bounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        // Sanitize min max
        if (minX > maxX) {
            int temp = minX;
            minX = maxX;
            maxX = temp;
        }

        if (minY > maxY) {
            int temp = minY;
            minY = maxY;
            maxY = temp;
        }

        if (minZ > maxZ) {
            int temp = minZ;
            minZ = maxZ;
            maxZ = temp;
        }

        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMinZ() {
        return this.minZ;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxY() {
        return this.maxY;
    }

    public int getMaxZ() {
        return this.maxZ;
    }

    public int getWidth() {
        return this.maxX - this.minX;
    }

    public int getHeight() {
        return this.maxY - this.minY;
    }

    public int getLength() {
        return this.maxZ - this.minZ;
    }

    public int getVolume() {
        return this.getWidth() * this.getHeight() * this.getLength();
    }

    public boolean isInside(Bounds bounds) {
        return this.isInside(bounds.getMinX(), bounds.getMinY(), bounds.getMinZ()) && this.isInside(bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ());
    }

    public boolean isInside(BoundingBox box) {
        return this.isInside(box.getMinX(), box.getMinY(), box.getMinZ()) && this.isInside(box.getMaxX(), box.getMaxY(), box.getMaxZ());
    }

    public boolean isInside(Location location) {
        return this.isInside(location.getX(), location.getY(), location.getZ());
    }

    public boolean isInside(int x, int y, int z) {
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
    }

    public boolean isInside(double x, double y, double z) {
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
    }

    public boolean isInside(int x, int z) {
        return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ;
    }

    public boolean isInside(double x, double z) {
        return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ;
    }

    public BoundingBox toBoundingBox() {
        return new BoundingBox(
                this.minX,
                this.minY,
                this.minZ,
                this.maxX,
                this.maxY,
                this.maxZ
        );
    }
}
