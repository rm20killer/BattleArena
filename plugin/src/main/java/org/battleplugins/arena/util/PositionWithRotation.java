package org.battleplugins.arena.util;

import org.battleplugins.arena.config.ArenaOption;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a position with a rotation.
 */
public class PositionWithRotation {
    @ArenaOption(name = "x", description = "The X position of the spawn.", required = true)
    private double x;
    @ArenaOption(name = "y", description = "The Y position of the spawn.", required = true)
    private double y;
    @ArenaOption(name = "z", description = "The Z position of the spawn.", required = true)
    private double z;
    @ArenaOption(name = "yaw", description = "The yaw of the spawn.")
    private float yaw;
    @ArenaOption(name = "pitch", description = "The pitch of the spawn.")
    private float pitch;

    public PositionWithRotation() {
    }

    public PositionWithRotation(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public PositionWithRotation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Location toLocation(World world) {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }
 }
