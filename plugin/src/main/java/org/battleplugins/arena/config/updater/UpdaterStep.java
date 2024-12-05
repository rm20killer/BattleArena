package org.battleplugins.arena.config.updater;

import org.bukkit.configuration.ConfigurationSection;

/**
 * An updater step for updating a config file across versions.
 */
public interface UpdaterStep<T> {

    /**
     * Updates the config file to the specified version.
     *
     * @param config the config
     * @param instance the config instance
     */
    void update(ConfigurationSection config, T instance);
}
