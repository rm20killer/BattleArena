package org.battleplugins.arena.messages;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.battleplugins.arena.BattleArena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageLoader {
    private static final MiniMessage MINI_MESSAGE_STRICT = MiniMessage.builder()
            .strict(true)
            .build();

    private static final Map<String, Message> MESSAGES = new HashMap<>();
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%(.+?)%");

    public static void load(Path messagesPath) {
        Messages.init();

        File messagesFile = messagesPath.toFile();
        FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messagesConfig.options().copyDefaults(true);

        // Save default messages
        MESSAGES.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                // Save strictly in file
                .forEach(entry -> messagesConfig.addDefault(entry.getKey(), MINI_MESSAGE_STRICT.serialize(entry.getValue().getText())));

        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            BattleArena.getInstance().error("Failed to save default messages to file!", e);
            return;
        }

        // Load messages
        for (String key : messagesConfig.getKeys(false)) {
            Message message = MESSAGES.get(key);
            if (message == null) {
                BattleArena.getInstance().warn("Unknown message key {} in messages file! Skipping", key);
                continue;
            }

            String messageText = messagesConfig.getString(key);
            if (messageText == null) {
                BattleArena.getInstance().warn("Message key {} has no value in messages file! Skipping", key);
                continue;
            }

            message.setText(Messages.MINI_MESSAGE.deserialize(resolveMessage(messageText), Messages.RESOLVER));
        }
    }

    private static String resolveMessage(String messageText) {
        // Substitute in any placeholders
        if (!messageText.contains("%")) {
            return messageText;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(messageText);
        StringBuilder replacedText = new StringBuilder();
        int lastEnd = 0;

        // Find each match and replace accordingly
        while (matcher.find()) {
            // Group 1 contains the captured text inside % signs
            String match = matcher.group(1);

            Message replacementMessage = MESSAGES.get(match);
            if (replacementMessage == null) {
                // Ensure text still gets appended in this case
                replacedText.append(messageText, lastEnd, matcher.end());
                lastEnd = matcher.end();

                // TODO: Third party placeholder support
                BattleArena.getInstance().warn("Unknown message placeholder {} in message! Skipping", match);
                continue;
            }

            String replacement = resolveMessage(MINI_MESSAGE_STRICT.serialize(replacementMessage.getText()));

            // Append the text from the end of the last match to the current match
            replacedText.append(messageText, lastEnd, matcher.start());

            // Append the replacement
            replacedText.append(replacement);

            // Update lastEnd to the end of the current match
            lastEnd = matcher.end();
        }

        // Append the remaining text after the last match
        replacedText.append(messageText.substring(lastEnd));
        return replacedText.toString();
    }

    static Message register(Message message) {
        if (message.context) {
            throw new IllegalArgumentException("Cannot register a message with context!");
        }

        if (MESSAGES.containsKey(message.getTranslationKey())) {
            BattleArena.getInstance().warn("Message with translation key {}", " already exists! Not registering", message.getTranslationKey());
            return message;
        }

        MESSAGES.put(message.getTranslationKey(), message);
        return message;
    }
}
