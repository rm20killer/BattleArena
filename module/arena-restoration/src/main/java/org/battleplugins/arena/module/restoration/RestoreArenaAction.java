package org.battleplugins.arena.module.restoration;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.map.options.Bounds;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class RestoreArenaAction extends EventAction {

    public RestoreArenaAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
        if (!arena.isModuleEnabled(ArenaRestoration.ID)) {
            return;
        }

        if (!(competition instanceof LiveCompetition<?> liveCompetition)) {
            return; // Cannot restore a non-live competition
        }

        Optional<ArenaRestoration> moduleOpt = arena.getPlugin()
                .<ArenaRestoration>module(ArenaRestoration.ID)
                .map(module -> module.initializer(ArenaRestoration.class));

        // No restoration module (should never happen)
        if (moduleOpt.isEmpty()) {
            return;
        }

        Bounds bounds = liveCompetition.getMap().getBounds();
        if (bounds == null) {
            // No bounds
            arena.getPlugin().warn("Could not restore map {} for arena {} as the bounds are not defined!", competition.getMap().getName(), arena.getName());
            return;
        }

        ArenaRestoration module = moduleOpt.get();
        Path path = module.getSchematicPath(arena, competition);
        if (Files.notExists(path)) {
            // No schematic found
            arena.getPlugin().warn("Could not restore map {} for arena {} as no schematic was found!", competition.getMap().getName(), arena.getName());
            return;
        }

        // Restore the arena
        Clipboard clipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(path.toFile());
        if (format == null) {
            // Invalid format
            arena.getPlugin().warn("Could not restore map {} for arena {} as the schematic format is invalid!", competition.getMap().getName(), arena.getName());
            return;
        }

        try (ClipboardReader reader = format.getReader(Files.newInputStream(path))) {
            clipboard = reader.read();
        } catch (IOException e) {
            // Error reading schematic
            arena.getPlugin().error("Failed to restore map {} for arena {} due to an error reading the schematic!", competition.getMap().getName(), arena.getName(), e);
            return;
        }

        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(liveCompetition.getMap().getWorld()))) {
            Operation operation = new ClipboardHolder(clipboard).createPaste(session)
                    .to(BlockVector3.at(bounds.getMinX(), bounds.getMinY(), bounds.getMinZ()))
                    .build();

            Operations.complete(operation);
        } catch (WorldEditException e) {
            // Error restoring schematic
            arena.getPlugin().error("Failed to restore map {} for arena {} due to an error restoring the schematic!", competition.getMap().getName(), arena.getName(), e);
        }
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        // No-op
    }
}
