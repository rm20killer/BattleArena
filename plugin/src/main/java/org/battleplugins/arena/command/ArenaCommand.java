package org.battleplugins.arena.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ArenaCommand {
    String[] commands() default { };
    String[] subCommands() default { };

    int minArgs() default 0;
    int maxArgs() default -1;

    boolean overrideDisabled() default false;
    boolean requiresOp() default false;

    String permissionNode() default "";

    String description() default "";
}