package org.battleplugins.arena.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an object that has a documentation
 * associated with it.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentationSource {

    /**
     * The URL to the documentation for
     * this object.
     *
     * @return the URL to the documentation
     */
    String value();
}
