package org.battleplugins.arena.options.types;

import org.battleplugins.arena.options.ArenaOption;

import java.util.Locale;
import java.util.Map;

public class EnumArenaOption<T extends Enum<T>> extends ArenaOption {
    private final Class<T> enumClazz;
    private final String key;

    public EnumArenaOption(Map<String, String> params, Class<T> enumClazz, String requiredKey) {
        super(params, requiredKey);

        this.enumClazz = enumClazz;
        this.key = requiredKey;
    }

    public T getOption() {
        return Enum.valueOf(this.enumClazz, this.get(this.key).toUpperCase(Locale.ROOT));
    }
}
