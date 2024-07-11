package org.battleplugins.arena.event.action;

import org.battleplugins.arena.config.DocumentationSource;
import org.battleplugins.arena.event.action.types.BroadcastAction;
import org.battleplugins.arena.event.action.types.ChangeGamemodeAction;
import org.battleplugins.arena.event.action.types.ChangeRoleAction;
import org.battleplugins.arena.event.action.types.ClearEffectsAction;
import org.battleplugins.arena.event.action.types.ClearInventoryAction;
import org.battleplugins.arena.event.action.types.DelayAction;
import org.battleplugins.arena.event.action.types.FlightAction;
import org.battleplugins.arena.event.action.types.GiveEffectsAction;
import org.battleplugins.arena.event.action.types.GiveItemAction;
import org.battleplugins.arena.event.action.types.HealthAction;
import org.battleplugins.arena.event.action.types.JoinRandomTeamAction;
import org.battleplugins.arena.event.action.types.KillEntitiesAction;
import org.battleplugins.arena.event.action.types.LeaveAction;
import org.battleplugins.arena.event.action.types.PlaySoundAction;
import org.battleplugins.arena.event.action.types.ResetStateAction;
import org.battleplugins.arena.event.action.types.RespawnAction;
import org.battleplugins.arena.event.action.types.RestoreAction;
import org.battleplugins.arena.event.action.types.RunCommandAction;
import org.battleplugins.arena.event.action.types.SendMessageAction;
import org.battleplugins.arena.event.action.types.StoreAction;
import org.battleplugins.arena.event.action.types.TeardownAction;
import org.battleplugins.arena.event.action.types.TeleportAction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents an event action type in an arena.
 *
 * @param <T> the type of event action
 */
@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/action-reference")
public final class EventActionType<T extends EventAction> {
    private static final Map<String, EventActionType<?>> ACTION_TYPES = new HashMap<>();

    public static final EventActionType<BroadcastAction> BROADCAST = new EventActionType<>("broadcast", BroadcastAction.class, BroadcastAction::new);
    public static final EventActionType<ChangeGamemodeAction> CHANGE_GAMEMODE = new EventActionType<>("change-gamemode", ChangeGamemodeAction.class, ChangeGamemodeAction::new);
    public static final EventActionType<ChangeRoleAction> CHANGE_ROLE = new EventActionType<>("change-role", ChangeRoleAction.class, ChangeRoleAction::new);
    public static final EventActionType<ClearInventoryAction> CLEAR_INVENTORY = new EventActionType<>("clear-inventory", ClearInventoryAction.class, ClearInventoryAction::new);
    public static final EventActionType<ClearEffectsAction> CLEAR_EFFECTS = new EventActionType<>("clear-effects", ClearEffectsAction.class, ClearEffectsAction::new);
    public static final EventActionType<DelayAction> DELAY = new EventActionType<>("delay", DelayAction.class, DelayAction::new);
    public static final EventActionType<FlightAction> FLIGHT = new EventActionType<>("flight", FlightAction.class, FlightAction::new);
    public static final EventActionType<GiveEffectsAction> GIVE_EFFECTS = new EventActionType<>("give-effects", GiveEffectsAction.class, GiveEffectsAction::new);
    public static final EventActionType<GiveItemAction> GIVE_ITEM = new EventActionType<>("give-item", GiveItemAction.class, GiveItemAction::new);
    public static final EventActionType<HealthAction> HEALTH = new EventActionType<>("health", HealthAction.class, HealthAction::new);
    public static final EventActionType<JoinRandomTeamAction> JOIN_RANDOM_TEAM = new EventActionType<>("join-random-team", JoinRandomTeamAction.class, JoinRandomTeamAction::new);
    public static final EventActionType<KillEntitiesAction> KILL_ENTITIES = new EventActionType<>("kill-entities", KillEntitiesAction.class, KillEntitiesAction::new);
    public static final EventActionType<LeaveAction> LEAVE = new EventActionType<>("leave", LeaveAction.class, LeaveAction::new);
    public static final EventActionType<PlaySoundAction> PLAY_SOUND = new EventActionType<>("play-sound", PlaySoundAction.class, PlaySoundAction::new);
    public static final EventActionType<ResetStateAction> RESET_STATE = new EventActionType<>("reset-state", ResetStateAction.class, ResetStateAction::new);
    public static final EventActionType<RespawnAction> RESPAWN = new EventActionType<>("respawn", RespawnAction.class, RespawnAction::new);
    public static final EventActionType<RestoreAction> RESTORE = new EventActionType<>("restore", RestoreAction.class, RestoreAction::new);
    public static final EventActionType<RunCommandAction> RUN_COMMAND = new EventActionType<>("run-command", RunCommandAction.class, RunCommandAction::new);
    public static final EventActionType<SendMessageAction> SEND_MESSAGE = new EventActionType<>("send-message", SendMessageAction.class, SendMessageAction::new);
    public static final EventActionType<StoreAction> STORE = new EventActionType<>("store", StoreAction.class, StoreAction::new);
    public static final EventActionType<TeardownAction> TEARDOWN = new EventActionType<>("teardown", TeardownAction.class, TeardownAction::new);
    public static final EventActionType<TeleportAction> TELEPORT = new EventActionType<>("teleport", TeleportAction.class, TeleportAction::new);

    private final String name;
    private final Class<T> clazz;
    private final Function<Map<String, String>, T> factory;

    EventActionType(String name, Class<T> clazz, Function<Map<String, String>, T> factory) {
        this.name = name;
        this.clazz = clazz;
        this.factory = factory;

        ACTION_TYPES.put(name, this);
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getActionType() {
        return this.clazz;
    }

    public T create(Map<String, String> params) {
        return this.factory.apply(params);
    }

    @Nullable
    public static EventActionType<?> get(String name) {
        return ACTION_TYPES.get(name);
    }

    public static <T extends EventAction> EventActionType<T> create(String name, Class<T> clazz, Function<Map<String, String>, T> factory) {
        return new EventActionType<>(name, clazz, factory);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EventActionType<?> that = (EventActionType<?>) object;
        return Objects.equals(this.clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clazz);
    }

    public static Set<EventActionType<?>> values() {
        return Set.copyOf(ACTION_TYPES.values());
    }
}
