package org.battleplugins.arena;

import org.bukkit.plugin.Plugin;

record ArenaType(Plugin plugin, Class<? extends Arena> arenaClass) {
}
