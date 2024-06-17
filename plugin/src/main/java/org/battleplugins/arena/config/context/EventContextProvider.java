package org.battleplugins.arena.config.context;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.config.SingularValueParser;
import org.battleplugins.arena.event.ArenaEventType;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.event.action.EventActionType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventContextProvider implements ContextProvider<Map<ArenaEventType<?>, List<EventAction>>> {

    @Override
    public Map<ArenaEventType<?>, List<EventAction>> provideInstance(ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) {
        if (!Map.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from Map when loading events!");
        }

        ConfigurationSection configurationSection = configuration.getConfigurationSection(name);
        if (option.required() && configurationSection == null) {
            throw new ParseException("Failed to find configuration section " + name + " in configuration section " + configuration.getName());
        } else if (!option.required() && configurationSection == null) {
            return Map.of();
        }

        Map<ArenaEventType<?>, List<EventAction>> eventActions = new LinkedHashMap<>();
        for (String key : configurationSection.getKeys(false)) {
            ArenaEventType<?> eventType = ArenaEventType.get(key);
            if (eventType == null) {
                throw new ParseException("Unrecognized event detected (" + key + ") when loading configuration section " + configurationSection.getName());
            }

            List<String> actions = configurationSection.getStringList(key);
            for (String actionStr : actions) {
                SingularValueParser.ArgumentBuffer buffer = SingularValueParser.parseNamed(actionStr, SingularValueParser.BraceStyle.CURLY, ';');
                if (!buffer.hasNext()) {
                    throw new ParseException("No data found for EventAction");
                }

                SingularValueParser.Argument root = buffer.pop();
                if (!root.key().equals("root")) {
                    throw new ParseException("Expected root key for EventActionType, got " + root.key());
                }

                EventActionType<?> actionType = EventActionType.get(root.value());
                if (actionType == null) {
                    // TODO: Error here
                    continue;
                    // throw new ParseException("Unrecognized event action detected (" + root.key() + ") when loading configuration section " + configurationSection.getName());
                }

                Map<String, String> params = new LinkedHashMap<>();
                while (buffer.hasNext()) {
                    SingularValueParser.Argument argument = buffer.pop();
                    params.put(argument.key(), argument.value());
                }

                try {
                    EventAction action = actionType.create(params);
                    eventActions.computeIfAbsent(eventType, e -> new ArrayList<>()).add(action);
                } catch (IllegalArgumentException e) {
                    throw new ParseException("Failed to create event action " + root.value() + " with params " + params, e);
                }
            }
        }

        return eventActions;
    }
}
