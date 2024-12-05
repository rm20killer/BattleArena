package org.battleplugins.arena.util;

import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.command.BaseCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    public static void registerPermissions(String commandName, BaseCommandExecutor executor) {
        String rootPermissionNode = "battlearena.command." + commandName.toLowerCase(Locale.ROOT);
        String wildcardPermissionNode = rootPermissionNode + ".*";

        PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        // Register the root permission so it shows up in things such as LuckPerms UI.
        // Set to true by default so the user can see the command, however this is intentionally
        // done so server owners can fully revoke the permission, and it will not show up in tab complete.
        Permission rootPermission = pluginManager.getPermission(rootPermissionNode);
        if (rootPermission == null) {
            pluginManager.addPermission(rootPermission = new Permission(rootPermissionNode, PermissionDefault.TRUE));
        }

        Permission wildcardPermission = pluginManager.getPermission(wildcardPermissionNode);
        if (wildcardPermission == null) {
            pluginManager.addPermission(wildcardPermission = new Permission(wildcardPermissionNode, PermissionDefault.OP));
        }

        Set<String> childPermissions = new HashSet<>();
        for (Map.Entry<String, Set<BaseCommandExecutor.CommandWrapper>> entry : executor.getCommandWrappers().entrySet()) {
            for (BaseCommandExecutor.CommandWrapper wrapper : entry.getValue()) {
                String node = wrapper.getCommand().permissionNode();
                String permissionNode = executor.getPermissionNode(node);

                // Add our permission node to the parent permission
                if (permissionNode != null) {
                    childPermissions.add(permissionNode);
                }
            }
        }

        // Add all child permissions to the parent permission
        for (String childPermissionNode : childPermissions) {
            Permission childPermission = pluginManager.getPermission(childPermissionNode);
            if (childPermission == null) {
                pluginManager.addPermission(childPermission = new Permission(childPermissionNode, PermissionDefault.OP));
            }

            // For wildcard permissions, set to true as we want all permissions granted when the wildcard is present
            childPermission.addParent(wildcardPermission, true);
        }
    }
}
