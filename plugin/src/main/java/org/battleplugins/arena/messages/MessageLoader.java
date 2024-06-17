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

public class MessageLoader {
    private static final MiniMessage MINI_MESSAGE_STRICT = MiniMessage.builder()
            .strict(true)
            .build();

    private static final Map<String, Message> MESSAGES = new HashMap<>();

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

            message.setText(Messages.MINI_MESSAGE.deserialize(messageText, Messages.RESOLVER));
        }
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
