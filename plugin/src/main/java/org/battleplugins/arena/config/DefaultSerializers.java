package org.battleplugins.arena.config;

import org.battleplugins.arena.Arena;

final class DefaultSerializers {

    static void register() {
        ArenaConfigSerializer.registerSerializer(Arena.class, (node, section, type) -> {
            section.set(node, type.getName());
        });
    }
}
