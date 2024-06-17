package org.battleplugins.arena.options;

import org.battleplugins.arena.options.types.BooleanArenaOption;
import org.battleplugins.arena.options.types.EnumArenaOption;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ArenaOptionType<T extends ArenaOption> {
    private static final Map<String, ArenaOptionType<?>> OPTION_TYPES = new HashMap<>();

    public static final ArenaOptionType<BooleanArenaOption> BLOCK_BREAK = new ArenaOptionType<>("block-break", BooleanArenaOption::new);
    public static final ArenaOptionType<BooleanArenaOption> BLOCK_PLACE = new ArenaOptionType<>("block-place", BooleanArenaOption::new);
    public static final ArenaOptionType<BooleanArenaOption> BLOCK_INTERACT = new ArenaOptionType<>("block-interact", BooleanArenaOption::new);
    public static final ArenaOptionType<BooleanArenaOption> KEEP_INVENTORY = new ArenaOptionType<>("keep-inventory", BooleanArenaOption::new);
    public static final ArenaOptionType<BooleanArenaOption> KEEP_EXPERIENCE = new ArenaOptionType<>("keep-experience", BooleanArenaOption::new);

    public static final ArenaOptionType<EnumArenaOption<DamageOption>> DAMAGE_PLAYERS = new ArenaOptionType<>("damage-players", params -> new EnumArenaOption<>(params, DamageOption.class, "option"));
    public static final ArenaOptionType<EnumArenaOption<DamageOption>> DAMAGE_ENTITIES = new ArenaOptionType<>("damage-entities", params -> new EnumArenaOption<>(params, DamageOption.class, "option"));

    private final Function<Map<String, String>, T> factory;

    ArenaOptionType(String name, Function<Map<String, String>, T> factory) {
        this.factory = factory;

        OPTION_TYPES.put(name, this);
    }

    public T create(Map<String, String> params) {
        return this.factory.apply(params);
    }

    @Nullable
    public static ArenaOptionType<?> get(String name) {
        return OPTION_TYPES.get(name);
    }

    public static <T extends ArenaOption> ArenaOptionType<T> create(String name, Function<Map<String, String>, T> factory) {
        return new ArenaOptionType<>(name, factory);
    }
}
