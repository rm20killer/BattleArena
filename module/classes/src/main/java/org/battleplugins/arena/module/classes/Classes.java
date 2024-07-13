package org.battleplugins.arena.module.classes;

import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.event.arena.ArenaCreateExecutorEvent;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleContainer;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.battleplugins.arena.options.ArenaOptionType;
import org.battleplugins.arena.options.types.BooleanArenaOption;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * A module that adds classes to the arena.
 */
@ArenaModule(id = Classes.ID, name = "Classes", description = "Adds classes to BattleArena.", authors = "BattlePlugins")
public class Classes implements ArenaModuleInitializer {
    public static final String ID = "classes";

    public static final EventActionType<EquipClassAction> EQUIP_CLASS_ACTION = EventActionType.create("equip-class", EquipClassAction.class, EquipClassAction::new);
    public static final ArenaOptionType<BooleanArenaOption> CLASS_EQUIPPING_OPTION = ArenaOptionType.create("class-equipping", BooleanArenaOption::new);
    public static final ArenaOptionType<BooleanArenaOption> CLASS_EQUIP_ONLY_SELECTS_OPTION = ArenaOptionType.create("class-equip-only-selects", BooleanArenaOption::new);

    private ClassesConfig classes;

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        ArenaModuleContainer<Classes> container = event.getBattleArena()
                .<Classes>module(ID)
                .orElseThrow();

        Path dataFolder = event.getBattleArena().getDataFolder().toPath();
        Path classesPath = dataFolder.resolve("classes.yml");
        if (Files.notExists(classesPath)) {
            InputStream inputStream = container.getResource("classes.yml");
            try {
                Files.copy(inputStream, classesPath);
            } catch (Exception e) {
                event.getBattleArena().error("Failed to copy classes.yml to data folder!", e);
                container.disable("Failed to copy classes.yml to data folder!");
                return;
            }
        }

        Configuration classesConfig = YamlConfiguration.loadConfiguration(classesPath.toFile());
        try {
            this.classes = ArenaConfigParser.newInstance(classesPath, ClassesConfig.class, classesConfig, event.getBattleArena());
        } catch (ParseException e) {
            ParseException.handle(e);

            container.disable("Failed to parse classes.yml!");
        }
    }

    @EventHandler
    public void onCreateExecutor(ArenaCreateExecutorEvent event) {
        if (!event.getArena().isModuleEnabled(ID)) {
            return;
        }

        event.registerSubExecutor(new ClassesExecutor(this, event.getArena()));
    }

    @Nullable
    public ArenaClass getClass(String name) {
        return this.classes.getClasses().get(name);
    }

    public boolean isRequirePermission() {
        return this.classes.isRequirePermission();
    }

    public Map<String, ArenaClass> getClasses() {
        return this.classes.getClasses();
    }
}
