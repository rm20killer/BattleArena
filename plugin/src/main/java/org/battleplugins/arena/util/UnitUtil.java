package org.battleplugins.arena.util;

import org.battleplugins.arena.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class UnitUtil {

    public static String toUnitString(CommandSender viewer, int amount, TimeUnit unit) {
        switch (unit) {
            case SECONDS -> {
                if (amount == 1) {
                    return amount + " " + Messages.SECOND.asPlainText();
                } else {
                    return amount + " " + Messages.SECONDS.asPlainText();
                }
            }
            case MINUTES -> {
                if (amount == 1) {
                    return amount + " " + Messages.MINUTE.asPlainText();
                } else {
                    return amount + " " + Messages.MINUTES.asPlainText();
                }
            }

            case HOURS -> {
                if (amount == 1) {
                    return amount + " " + Messages.HOUR.asPlainText();
                } else {
                    return amount + " " + Messages.HOURS.asPlainText();
                }
            }
        }

        // Realistically, we will only ever be using the values above
        return unit.name().toLowerCase(Locale.ROOT);
    }
}
