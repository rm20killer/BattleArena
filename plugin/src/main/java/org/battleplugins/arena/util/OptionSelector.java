package org.battleplugins.arena.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OptionSelector {

    public static void sendOptions(Player player, List<Option> options, ClickEvent.Action action) {
        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            Component message = Component.text("[" + (i + 1) + "] ", Messages.SECONDARY_COLOR)
                    .append(option.message().toComponent().style(Style.style(Messages.PRIMARY_COLOR)));

            if (option.hoverMessage() != null) {
                message = message.hoverEvent(option.hoverMessage().toComponent());
            }

            player.sendMessage(message.clickEvent(ClickEvent.clickEvent(action, option.command())));
        }
    }

    public record Option(Message message, String command, @Nullable Message hoverMessage) {

        public Option(Message message, String command) {
            this(message, command, Messages.CLICK_TO_SELECT);
        }
    }
}
