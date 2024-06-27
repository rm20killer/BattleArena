package org.battleplugins.arena.module;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Path;

public record ArenaModuleContainer<T>(Path path, ArenaModuleLoader loader, ArenaModule module, T mainClass) {

    public T initializer(Class<T> clazz) {
        return clazz.cast(this.mainClass);
    }

    public void disable(String reason) {
        this.loader.disableModule(new ModuleLoadException(this.module, reason));
    }

    @Nullable
    public InputStream getResource(String location) {
        return this.loader.getResource(this.path, location);
    }
}