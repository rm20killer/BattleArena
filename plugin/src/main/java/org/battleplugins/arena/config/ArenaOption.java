package org.battleplugins.arena.config;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.config.context.ContextProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an option for an {@link Arena}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ArenaOption {

    /**
     * The name of the option.
     *
     * @return the name of the option
     */
    String name();

    /**
     * The description of the option.
     *
     * @return the description of the option
     */
    String description() default "";

    /**
     * If the option is required.
     *
     * @return if the option is required
     */
    boolean required() default false;

    /**
     * The context provider for the option.
     *
     * @return the context provider for the option
     */
    Class<? extends ContextProvider> contextProvider() default ContextProvider.class;
}
