package org.battleplugins.arena.config.updater;

import java.util.Map;

/**
 * An updater for updating a config file across versions.
 */
public interface ConfigUpdater<T> {

    /**
     * Builds the updater for the config file.
     * <p>
     * The key of the map will be the version to update
     * to, and the value will be the updater step to update
     * the config file.
     *
     * @return the updater for the config file
     */
    Map<String, UpdaterStep<T>> buildUpdaters();
}
