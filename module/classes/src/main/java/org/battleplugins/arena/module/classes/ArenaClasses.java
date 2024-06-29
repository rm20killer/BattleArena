package org.battleplugins.arena.module.classes;

import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;
import org.battleplugins.arena.config.Scoped;

import java.util.Map;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/classes")
public class ArenaClasses {
    @Scoped
    BattleArena plugin;

    @ArenaOption(name = "classes", description = "The classes that will be added to the arena.")
    private Map<String, ArenaClass> classes;

    public Map<String, ArenaClass> getClasses() {
        return this.classes;
    }
}
