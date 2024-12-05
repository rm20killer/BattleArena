package org.battleplugins.arena.config;

import org.battleplugins.arena.config.updater.ConfigUpdater;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used over a configurable class to specify
 * which updater should be used to update the configuration
 * across versions.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Updater {

    /**
     * The class of the updater.
     *
     * @return the class of the updater
     */
    Class<? extends ConfigUpdater<?>> value();
}
