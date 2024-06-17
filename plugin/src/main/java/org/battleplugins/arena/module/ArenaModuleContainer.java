package org.battleplugins.arena.module;

public record ArenaModuleContainer<T>(ArenaModuleLoader loader, ArenaModule module, T mainClass) {

    public T initializer(Class<T> clazz) {
        return clazz.cast(this.mainClass);
    }

    public void disable(String reason) {
        this.loader.disableModule(new ModuleLoadException(this.module, reason));
    }
}