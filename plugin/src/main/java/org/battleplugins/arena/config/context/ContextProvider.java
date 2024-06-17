package org.battleplugins.arena.config.context;

import org.battleplugins.arena.config.ArenaOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public interface ContextProvider<T> {

    T provideInstance(ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope);
}
