package org.battleplugins.arena.module.classes;

import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.event.arena.ArenaCreateExecutorEvent;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.battleplugins.arena.options.ArenaOptionType;
import org.battleplugins.arena.options.types.BooleanArenaOption;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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
    public static final ArenaOptionType<BooleanArenaOption> CLASS_EQUIP_ONLY_SELECTS = ArenaOptionType.create("class-equip-only-selects", BooleanArenaOption::new);

    private ArenaClasses classes;

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        Path dataFolder = event.getBattleArena().getDataFolder().toPath();
        Path classesPath = dataFolder.resolve("classes.yml");
        if (Files.notExists(classesPath)) {
            InputStream inputStream = getClass().getResourceAsStream("/classes.yml");
            try {
                Files.copy(inputStream, classesPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Configuration classesConfig = YamlConfiguration.loadConfiguration(new File(dataFolder.toFile(), "classes.yml"));
        this.classes = ArenaConfigParser.newInstance(ArenaClasses.class, classesConfig, event.getBattleArena());
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

    public Map<String, ArenaClass> getClasses() {
        return this.classes.getClasses();
    }
}
