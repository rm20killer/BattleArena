package org.battleplugins.arena.util;

public interface LoggerHolder {

    default void info(String message) {
        this.getSLF4JLogger().info(message);
    }

    default void info(String message, Object... args) {
        this.getSLF4JLogger().info(message, args);
    }

    default void error(String message) {
        this.getSLF4JLogger().error(message);
    }

    default void error(String message, Object... args) {
        this.getSLF4JLogger().error(message, args);
    }

    default void warn(String message) {
        this.getSLF4JLogger().warn(message);
    }

    default void warn(String message, Object... args) {
        this.getSLF4JLogger().warn(message, args);
    }

    default void debug(String message, Object... args) {
        if (this.isDebugMode()) {
            this.getSLF4JLogger().info("[DEBUG] " + message, args);
        }
    }

    org.slf4j.Logger getSLF4JLogger();

    boolean isDebugMode();

    void setDebugMode(boolean debugMode);
}
