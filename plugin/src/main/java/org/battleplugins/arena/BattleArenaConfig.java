package org.battleplugins.arena;

import org.battleplugins.arena.competition.event.EventOptions;
import org.battleplugins.arena.config.ArenaOption;

import java.util.List;
import java.util.Map;

public class BattleArenaConfig {

    @ArenaOption(name = "config-version", description = "The version of the config.", required = true)
    private String configVersion;

    @ArenaOption(name = "backup-inventories", description = "Whether player inventories should be backed up when joining competitions.", required = true)
    private boolean backupInventories;

    @ArenaOption(name = "max-backups", description = "The maximum number of backups to save for each player.", required = true)
    private int maxBackups;

    @ArenaOption(name = "max-dynamic-maps", description = "The maximum number of dynamic maps an Arena can have allocated at once.", required = true)
    private int maxDynamicMaps;

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

    public Map<String, List<EventOptions>> getEvents() {
        return this.events;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }
}
