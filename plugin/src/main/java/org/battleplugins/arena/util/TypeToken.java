package org.battleplugins.arena.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T> {
    private final Type type;

    protected TypeToken() {
        this.type = getSuperclassTypeParameter(getClass());
    }

    private TypeToken(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public static <T> TypeToken<T> of(Type type) {
        return new TypeToken<>(type);
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }

        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }
}
