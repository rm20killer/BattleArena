package org.battleplugins.arena.competition.map;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.map.options.Bounds;
import org.battleplugins.arena.competition.map.options.Spawns;
import org.jetbrains.annotations.Nullable;

/**
 * A factory for creating maps.
 */
public class MapFactory {
    private final Class<? extends LiveCompetitionMap> mapClass;
    private final Provider<?> provider;

    private <M extends LiveCompetitionMap> MapFactory(Class<M> mapClass, Provider<M> provider) {
        this.mapClass = mapClass;
        this.provider = provider;
    }

    /**
     * Gets the class of the map.
     *
     * @return the class of the map
     */
    public Class<? extends LiveCompetitionMap> getMapClass() {
        return this.mapClass;
    }

    /**
     * Creates a new {@link LiveCompetitionMap map} from the given parameters.
     *
     * @param name the name of the map
     * @param arena the arena this map is for
     * @param type the type of map
     * @param world the world the map is located in
     * @param bounds the bounds of the map
     * @param spawns the spawn locations
     * @return the created map
     */
    public LiveCompetitionMap create(String name, Arena arena, MapType type, String world, @Nullable Bounds bounds, @Nullable Spawns spawns) {
        return this.provider.create(name, arena, type, world, bounds, spawns);
    }

    public static <M extends LiveCompetitionMap> MapFactory create(Class<M> mapClass, Provider<M> provider) {
        return new MapFactory(mapClass, provider);
    }

    public interface Provider<M extends LiveCompetitionMap> {

        /**
         * Creates a new {@link M map} from the given parameters.
         *
         * @param name the name of the map
         * @param arena the arena this map is for
         * @param type the type of map
         * @param world the world the map is located in
         * @param bounds the bounds of the map
         * @param spawns the spawn locations
         * @return the created map
         */
        M create(String name, Arena arena, MapType type, String world, @Nullable Bounds bounds, @Nullable Spawns spawns);
    }
}
