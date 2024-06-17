package org.battleplugins.arena.config;

public class BattleArenaConfig {

    @ArenaOption(name = "config-version", required = true, description = "The version of the config.")
    private String configVersion;

    @ArenaOption(name = "backup-inventories", required = true, description = "Whether player inventories should be backed up when joining competitions.")
    private boolean backupInventories;

    @ArenaOption(name = "max-backups", required = true, description = "The maximum number of backups to save for each player.")
    private int maxBackups;

    @ArenaOption(name = "max-dynamic-maps", required = true, description = "The maximum number of dynamic maps an Arena can have allocated at once.")
    private int maxDynamicMaps;

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
}
