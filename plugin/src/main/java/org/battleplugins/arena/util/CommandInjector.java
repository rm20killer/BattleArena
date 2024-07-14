package org.battleplugins.arena.util;

import org.battleplugins.arena.BattleArena;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CommandInjector {

    public static PluginCommand inject(String arenaName, String commandName, String... aliases) {
        return inject(arenaName, commandName, "The main command for the " + arenaName + " arena!", aliases);
    }

    public static PluginCommand inject(String headerName, String commandName, String description, String... aliases) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCommand = constructor.newInstance(commandName, BattleArena.getInstance());
            pluginCommand.setAliases(List.of(aliases));
            pluginCommand.setDescription(description);
            pluginCommand.setPermission("battlearena.command." + commandName);

            Bukkit.getCommandMap().register(commandName, "battlearena", pluginCommand);
            return pluginCommand;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to construct PluginCommand " + headerName, e);
        }
    }
}
