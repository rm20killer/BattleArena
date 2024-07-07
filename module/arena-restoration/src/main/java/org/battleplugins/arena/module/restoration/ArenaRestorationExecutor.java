package org.battleplugins.arena.module.restoration;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.command.ArenaCommand;
import org.battleplugins.arena.command.SubCommandExecutor;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.map.options.Bounds;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArenaRestorationExecutor implements SubCommandExecutor {
    private final ArenaRestoration module;
    private final Arena arena;

    public ArenaRestorationExecutor(ArenaRestoration module, Arena arena) {
        this.module = module;
        this.arena = arena;
    }

    @ArenaCommand(commands = "schematic", description = "Creates a schematic for the specified arena from the map bounds.", permissionNode = "region")
    public void region(Player player, Competition<?> competition) {
        if (!(competition instanceof LiveCompetition<?> liveCompetition)) {
            return; // Cannot restore a non-live competition
        }

        Bounds bounds = liveCompetition.getMap().getBounds();
        if (bounds == null) {
            // No bounds
            ArenaRestoration.NO_BOUNDS.send(player);
            return;
        }

        CuboidRegion region = new CuboidRegion(BlockVector3.at(bounds.getMinX(), bounds.getMinY(), bounds.getMinZ()), BlockVector3.at(bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ()));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        ForwardExtentCopy copy = new ForwardExtentCopy(BukkitAdapter.adapt(liveCompetition.getMap().getWorld()), region, clipboard, region.getMinimumPoint());

        try {
            Operations.complete(copy);
        } catch (WorldEditException e) {
            ArenaRestoration.FAILED_TO_CREATE_SCHEMATIC.send(player);
            this.arena.getPlugin().error("Failed to create schematic for map {} in arena {}", competition.getMap().getName(), this.arena.getName(), e);
            return;
        }

        // Create schematic from clipboard
        Path path = this.module.getSchematicPath(this.arena, competition);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                ArenaRestoration.FAILED_TO_CREATE_SCHEMATIC.send(player);
                this.arena.getPlugin().error("Failed to create schematic for map {} in arena {}", competition.getMap().getName(), this.arena.getName(), e);
                return;
            }
        }

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(Files.newOutputStream(path))) {
            writer.write(clipboard);
            ArenaRestoration.SCHEMATIC_CREATED.send(player, competition.getMap().getName());
        } catch (IOException e) {
            ArenaRestoration.FAILED_TO_CREATE_SCHEMATIC.send(player);
            this.arena.getPlugin().error("Failed to create schematic for map {} in arena {}", competition.getMap().getName(), this.arena.getName(), e);
        }
    }
}
