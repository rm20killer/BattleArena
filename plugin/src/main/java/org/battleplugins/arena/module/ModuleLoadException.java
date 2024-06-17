package org.battleplugins.arena.module;

public class ModuleLoadException extends RuntimeException {
    private final ArenaModule module;

    public ModuleLoadException(ArenaModule module, String message) {
        super(message);

        this.module = module;
    }

    public ModuleLoadException(ArenaModule module, Exception exception) {
        super(exception);

        this.module = module;
    }

    public ArenaModule getModule() {
        return this.module;
    }
}
