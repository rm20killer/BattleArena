package org.battleplugins.arena;

import org.battleplugins.arena.competition.event.EventOptions;
import org.battleplugins.arena.config.ArenaOption;

import java.util.List;
import java.util.Map;

/**
 * Represents the BattleArena configuration.
 */
public class BattleArenaConfig {

    @ArenaOption(name = "config-version", description = "The version of the config.", required = true)
    private String configVersion;

    @ArenaOption(name = "backup-inventories", description = "Whether player inventories should be backed up when joining competitions.", required = true)
    private boolean backupInventories;

    @ArenaOption(name = "max-backups", description = "The maximum number of backups to save for each player.", required = true)
    private int maxBackups;

    @ArenaOption(name = "max-dynamic-maps", description = "The maximum number of dynamic maps an Arena can have allocated at once.", required = true)
    private int maxDynamicMaps;

    @ArenaOption(name = "disabled-modules", description = "Modules that are disabled by default.")
    private List<String> disabledModules;

    @ArenaOption(name = "events", description = "The configured events.", required = true)
    private Map<String, List<EventOptions>> events;

    @ArenaOption(name = "debug-mode", description = "Whether debug mode is enabled.")
    private boolean debugMode;

    public String getConfigVersion() {
        return this.configVersion;
    }

    public boolean isBackupInventories() {
        return this.backupInventories;
    }

    public int getMaxBackups() {
        return this.maxBackups;
    }

    public int getMaxDynamicMaps() {
        return this.maxDynamicMaps;
    }

    public List<String> getDisabledModules() {
        return this.disabledModules == null ? List.of() : List.copyOf(this.disabledModules);
    }

    public Map<String, List<EventOptions>> getEvents() {
        return Map.copyOf(this.events);
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }
}
