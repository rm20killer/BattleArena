package org.battleplugins.arena.module.classes;

import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;
import org.battleplugins.arena.config.Scoped;

import java.util.Map;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/classes")
public class ClassesConfig {
    @Scoped
    BattleArena plugin;

    @ArenaOption(name = "require-permission", description = "Whether players need specific permission nodes in order to equip classes.", required = true)
    private boolean requirePermission;

    @ArenaOption(name = "classes", description = "The classes that will be added to the arena.")
    private Map<String, ArenaClass> classes;

    public boolean isRequirePermission() {
        return this.requirePermission;
    }

    public Map<String, ArenaClass> getClasses() {
        return this.classes == null ? Map.of() : Map.copyOf(this.classes);
    }
}
