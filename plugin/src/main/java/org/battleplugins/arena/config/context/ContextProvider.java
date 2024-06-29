package org.battleplugins.arena.config.context;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface ContextProvider<T> {

    T provideInstance(@Nullable Path sourceFile, ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) throws ParseException;
}
