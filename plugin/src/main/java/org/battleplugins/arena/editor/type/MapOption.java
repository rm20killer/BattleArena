package org.battleplugins.arena.editor.type;

public enum MapOption implements EditorKey {
    NAME("name"),
    TYPE("type"),
    MIN_POS("minPos"),
    MAX_POS("maxPos"),
    WAITROOM_SPAWN("waitroomSpawn"),
    SPECTATOR_SPAWN("spectatorSpawn"),
    TEAM_SPAWNS("teamSpawns");

    private final String key;

    MapOption(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
