package org.battleplugins.arena.util;

import org.battleplugins.arena.config.ArenaOption;

import java.lang.reflect.Field;

public class FieldUtil {

    public static <T> void copyFields(T oldInstance, T newInstance) {
        for (Field field : oldInstance.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ArenaOption.class)) {
                continue;
            }

            field.setAccessible(true);

            try {
                field.set(newInstance, field.get(oldInstance));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to copy field " + field.getName(), e);
            }
        }
    }
}
