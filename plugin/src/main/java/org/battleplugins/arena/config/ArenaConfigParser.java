package org.battleplugins.arena.config;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.battleplugins.arena.config.context.ContextProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ArenaConfigParser {
    private static final Map<Class<?>, ContextProvider<?>> CONTEXT_PROVIDERS = new HashMap<>();
    private static final Map<Class<?>, Supplier<?>> INSTANCE_SUPPLIER = new HashMap<>();
    private static final Map<Class<?>, Function<Object, Object>> OBJECT_PROVIDERS = new HashMap<>();

    static {
        DefaultParsers.register();
    }

    public static <T> T newInstance(Class<T> type, ConfigurationSection configuration) throws ParseException {
        return newInstance(type, configuration, null);
    }

    public static <T> T newInstance(Class<T> type, ConfigurationSection configuration, @Nullable Object scope) throws ParseException {
        return newInstance(type, configuration, scope, null);
    }

    public static <T> T newInstance(Class<T> type, ConfigurationSection configuration, @Nullable Object scope, @Nullable Object id) throws ParseException {
        T instance = newClassInstance(type);
        try {
            populateFields(instance, configuration, scope, id);
            if (instance instanceof PostProcessable postProcessable) {
                postProcessable.postProcess();
            }

            return instance;
        } catch (ParseException e) {
            throw e;
        } catch (Throwable t) {
            throw new ParseException("Failed to post-process instance of class " + type.getName(), t);
        }
    }

    private static void populateFields(Object instance, ConfigurationSection configuration, @Nullable Object scope, @Nullable Object id) throws ParseException {
        for (Field field : FieldUtils.getAllFieldsList(instance.getClass())) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Scoped.class)) {
                if (scope == null) {
                    throw new ParseException("Scope annotation found on field " + field.getName() + " in class " + instance.getClass().getName() + " but no scope was provided");
                }

                try {
                    field.set(instance, scope);
                } catch (IllegalAccessException e) {
                    throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
                }

                continue;
            }

            if (field.isAnnotationPresent(Id.class)) {
                if (id == null) {
                    throw new ParseException("Id annotation found on field " + field.getName() + " in class " + instance.getClass().getName() + " but no id was provided");
                }

                try {
                    field.set(instance, id);
                } catch (IllegalAccessException e) {
                    throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
                }

                continue;
            }

            if (!field.isAnnotationPresent(ArenaOption.class)) {
                continue;
            }

            ArenaOption arenaOption = field.getDeclaredAnnotation(ArenaOption.class);
            String name = arenaOption.name();
            boolean required = arenaOption.required();

            // Ensure we have an instance of this parameter
            if (required && !configuration.contains(name)) {
                throw new ParseException("Required option " + name + " not found in configuration section " + configuration.getName());
            }

            // Get the type from the configuration
            Class<?> type = field.getType();

            // Check if we have a context provider for this field
            if (CONTEXT_PROVIDERS.containsKey(arenaOption.contextProvider())) {
                try {
                    field.set(instance, CONTEXT_PROVIDERS.get(arenaOption.contextProvider())
                            .provideInstance(arenaOption, type, configuration, name, scope));
                } catch (IllegalAccessException e) {
                    throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
                }

                continue;
            }

            // First, let's check if our parameter is a primitive
            if (type.isPrimitive()) {
                populatePrimitive(name, type, field, instance, configuration);
            } else if (type == String.class) {
                try {
                    field.set(instance, configuration.getString(name));
                } catch (IllegalAccessException e) {
                    throw new ParseException("Failed to set string field " + field.getName() + " in class " + instance.getClass().getName(), e);
                }
            } else if (type.isEnum()) {
                try {
                    field.set(instance, Enum.valueOf((Class<? extends Enum>) field.getGenericType(), configuration.getString(name).toUpperCase(Locale.ROOT)));
                } catch (IllegalAccessException e) {
                    throw new ParseException("Failed to set enum field " + field.getName() + " in class " + instance.getClass().getName(), e);
                }
            } else {
                // Value is not a primitive, let's check to see if we have a provider for it
                if (OBJECT_PROVIDERS.containsKey(type)) {
                    try {
                        field.set(instance, OBJECT_PROVIDERS.get(type).apply(configuration.get(name)));
                    } catch (IllegalAccessException e) {
                        throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
                    }
                } else {
                    // No object, let's parse for lists/maps now
                    if (List.class.isAssignableFrom(type)) {
                        List<?> list = configuration.getList(name);
                        if (list == null) {
                            continue;
                        }

                        List<ConfigurationSection> sections = toMemorySections(list);
                        if (sections.isEmpty() && !list.isEmpty()) {
                            // Sections are empty, but the list is not, so let's go through
                            // the list and check if we have any providers for the objects
                            List<Object> objectList = new ArrayList<>(list.size());

                            // Get the primitive type of the list
                            Class<?> listType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                            if (OBJECT_PROVIDERS.containsKey(listType)) {
                                Function<Object, Object> objectProvider = OBJECT_PROVIDERS.get(listType);
                                for (Object object : list) {
                                    objectList.add(objectProvider.apply(object));
                                }
                            } else {
                                // Assume it's a primitive
                                objectList.addAll(list);
                            }

                            try {
                                field.set(instance, objectList);
                            } catch (IllegalAccessException e) {
                                throw new ParseException("Failed to set list field " + field.getName() + " in class " + instance.getClass().getName(), e);
                            }
                        } else {
                            // Parse as object
                            List<Object> objects = new ArrayList<>(sections.size());
                            for (ConfigurationSection section : sections) {
                                objects.add(newInstance((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0], section, instance));
                            }

                            try {
                                field.set(instance, objects);
                            } catch (IllegalAccessException e) {
                                throw new ParseException("Failed to set list field " + field.getName() + " in class " + instance.getClass().getName(), e);
                            }
                        }
                    } else if (Map.class.isAssignableFrom(type)) {
                        ConfigurationSection configurationSection = configuration.getConfigurationSection(name);
                        if (configurationSection == null) {
                            continue;
                        }

                        Map<String, Object> map = new HashMap<>();
                        for (String key : configurationSection.getKeys(false)) {
                            ConfigurationSection section = null;
                            if (configurationSection.isConfigurationSection(key)) {
                                section = configurationSection.getConfigurationSection(key);
                            }

                            ParameterizedType mapType = (ParameterizedType) field.getGenericType();
                            Type value = mapType.getActualTypeArguments()[1];
                            Class<?> valueClass = (Class<?>) value;

                            // Check if the value is contained in our object deserializers
                            if (section == null && OBJECT_PROVIDERS.containsKey(valueClass)) {
                                map.put(key, OBJECT_PROVIDERS.get(valueClass).apply(configurationSection.get(key)));
                            } else {
                                if (section == null) {
                                    throw new ParseException("Failed to parse map field " + field.getName() + " in class " + instance.getClass().getName());
                                }

                                map.put(key, newInstance(valueClass, section, instance));
                            }
                        }

                        try {
                            field.set(instance, map);
                        } catch (IllegalAccessException e) {
                            throw new ParseException("Failed to set map field " + field.getName() + " in class " + instance.getClass().getName(), e);
                        }
                    } else {
                        // Unknown object! Let's try to parse it
                        ConfigurationSection configurationSection = configuration.getConfigurationSection(name);
                        if (configurationSection == null) {
                            continue;
                        }

                        try {
                            field.set(instance, newInstance(type, configurationSection, instance));
                        } catch (IllegalAccessException e) {
                            throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
                        }
                    }
                }
            }
        }
    }

    public static <T> void registerFactory(Class<T> clazz, Supplier<T> supplier) {
        INSTANCE_SUPPLIER.put(clazz, supplier);
    }

    @SuppressWarnings("unchecked")
    public static <T> void registerProvider(Class<T> clazz, Function<Object, T> provider) {
        OBJECT_PROVIDERS.put(clazz, (Function<Object, Object>) provider);
    }

    public static <T extends ContextProvider<?>> void registerContextProvider(Class<T> clazz, T provider) {
        CONTEXT_PROVIDERS.put(clazz, provider);
    }

    private static void populatePrimitive(String name, Class<?> type, Field field, Object instance, ConfigurationSection configuration) throws ParseException {
        if (type == boolean.class) {
            try {
                field.set(instance, configuration.getBoolean(name));
            } catch (IllegalAccessException e) {
                throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
            }
        } else if (type == int.class) {
            try {
                field.set(instance, configuration.getInt(name));
            } catch (IllegalAccessException e) {
                throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
            }
        } else if (type == double.class) {
            try {
                field.set(instance, configuration.getDouble(name));
            } catch (IllegalAccessException e) {
                throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
            }
        } else if (type == float.class) {
            try {
                field.set(instance, (float) configuration.getDouble(name));
            } catch (IllegalAccessException e) {
                throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
            }
        } else if (type == long.class) {
            try {
                field.set(instance, configuration.getLong(name));
            } catch (IllegalAccessException e) {
                throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
            }
        } else if (type == short.class) {
            try {
                field.set(instance, (short) configuration.getInt(name));
            } catch (IllegalAccessException e) {
                throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
            }
        } else if (type == byte.class) {
            try {
                field.set(instance, (byte) configuration.getInt(name));
            } catch (IllegalAccessException e) {
                throw new ParseException("Failed to set field " + field.getName() + " in class " + instance.getClass().getName(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T newClassInstance(Class<T> clazz) {
        if (INSTANCE_SUPPLIER.containsKey(clazz)) {
            return (T) INSTANCE_SUPPLIER.get(clazz).get();
        }

        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ParseException("Failed to instantiate class " + clazz.getName() + "! Did you forget a no-args constructor?", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<ConfigurationSection> toMemorySections(List<?> list) {
        List<ConfigurationSection> sections = new LinkedList<>();
        for (Object object : list) {
            if (object instanceof Map<?, ?> map) {
                MemoryConfiguration memoryConfig = new MemoryConfiguration();
                memoryConfig.addDefaults((Map<String, Object>) map);

                sections.add(memoryConfig);
            }
        }

        return sections;
    }
}
