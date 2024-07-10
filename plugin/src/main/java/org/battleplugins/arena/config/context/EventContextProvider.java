package org.battleplugins.arena.config.context;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.config.SingularValueParser;
import org.battleplugins.arena.event.ArenaEventType;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.options.ArenaOptionType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventContextProvider implements ContextProvider<Map<ArenaEventType<?>, List<EventAction>>> {

    @Override
    public Map<ArenaEventType<?>, List<EventAction>> provideInstance(@Nullable Path sourceFile, ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) throws ParseException {
        if (!Map.class.isAssignableFrom(type)) {
            throw new ParseException("Expected " + type.getName() + " to be assignable from Map when loading events!")
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(ArenaEventType.class)
                    .sourceFile(sourceFile);
        }

        ConfigurationSection configurationSection = configuration.getConfigurationSection(name);
        if (option.required() && configurationSection == null) {
            throw new ParseException("Failed to find configuration section " + name + " in configuration section " + configuration.getName())
                    .context("Type", type.getName())
                    .context("Name", name)
                    .context("Section", configuration.getName())
                    .cause(ParseException.Cause.MISSING_SECTION)
                    .type(ArenaEventType.class)
                    .userError()
                    .sourceFile(sourceFile);
        } else if (!option.required() && configurationSection == null) {
            return Map.of();
        }

        Map<ArenaEventType<?>, List<EventAction>> eventActions = new LinkedHashMap<>();
        for (String key : configurationSection.getKeys(false)) {
            ArenaEventType<?> eventType = ArenaEventType.get(key);
            if (eventType == null) {
                throw new ParseException("Unrecognized event detected (" + key + ") when loading configuration section " + configurationSection.getName())
                        .context("Section", configuration.getName())
                        .context("Provided event", key)
                        .context("Valid events", ArenaEventType.values().stream().map(ArenaEventType::getName).toList().toString())
                        .cause(ParseException.Cause.INVALID_VALUE)
                        .type(ArenaEventType.class)
                        .userError()
                        .sourceFile(sourceFile);
            }

            List<String> actions = configurationSection.getStringList(key);
            for (String actionStr : actions) {
                SingularValueParser.ArgumentBuffer buffer = SingularValueParser.parseNamed(actionStr, SingularValueParser.BraceStyle.CURLY, ';');
                if (!buffer.hasNext()) {
                    throw new ParseException("No actions found for EventAction")
                            .context("Section", configuration.getName())
                            .context("Provided actions", actions.toString())
                            .cause(ParseException.Cause.INVALID_VALUE)
                            .type(EventActionType.class)
                            .userError()
                            .sourceFile(sourceFile);
                }

                SingularValueParser.Argument root = buffer.pop();
                if (!root.key().equals("root")) {
                    throw new ParseException("Expected root key for EventActionType, got " + root.key())
                            .context("Section", configuration.getName())
                            .context("Provided key", root.key())
                            .cause(ParseException.Cause.INTERNAL_ERROR)
                            .type(EventActionType.class)
                            .sourceFile(sourceFile);
                }

                EventActionType<?> actionType = EventActionType.get(root.value());
                if (actionType == null) {
                    throw new ParseException("Unrecognized event action detected (" + root.value() + ") when loading configuration section " + configurationSection.getName())
                            .context("Section", configuration.getName())
                            .context("Provided action", root.value())
                            .context("Valid events", EventActionType.values().stream().map(EventActionType::getName).toList().toString())
                            .cause(ParseException.Cause.INVALID_VALUE)
                            .type(EventActionType.class)
                            .userError()
                            .sourceFile(sourceFile);
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
                    throw new ParseException("Failed to create event action " + root.value() + " with params " + params, e)
                            .context("Section", configuration.getName())
                            .context("Params", params.isEmpty() ? "none" : params.toString())
                            .context("Reason", e.getMessage())
                            .cause(ParseException.Cause.INTERNAL_ERROR)
                            .type(EventActionType.class)
                            .userError()
                            .sourceFile(sourceFile);
                }
            }
        }

        return eventActions;
    }
}
