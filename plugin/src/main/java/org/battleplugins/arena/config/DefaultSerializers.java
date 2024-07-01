package org.battleplugins.arena.config;

import org.battleplugins.arena.Arena;
import org.bukkit.block.data.BlockData;

final class DefaultSerializers {

    static void register() {
        ArenaConfigSerializer.registerSerializer(Arena.class, (node, section, type) -> {
            section.set(node, type.getName());
        });

        ArenaConfigSerializer.registerSerializer(BlockData.class, (node, section, type) -> {
            section.set(node, type.getAsString());
        });
    }
}
