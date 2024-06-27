package org.battleplugins.arena.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommandExecutor {
    default Object onVerifyArgument(CommandSender sender, String arg, Class<?> parameter) {
        return null;
    }

    default boolean onInvalidArgument(CommandSender sender, Class<?> parameter, String input) {
        return false;
    }

    default List<String> onVerifyTabComplete(String arg, Class<?> parameter) {
        return List.of();
    }

    default String getUsageString(Class<?> parameter) {
        return null;
    }
}
