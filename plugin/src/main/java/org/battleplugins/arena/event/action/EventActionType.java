package org.battleplugins.arena.event.action;

import org.battleplugins.arena.event.action.types.ChangeGamemodeAction;
import org.battleplugins.arena.event.action.types.ClearEffectsAction;
import org.battleplugins.arena.event.action.types.DelayAction;
import org.battleplugins.arena.event.action.types.FlightAction;
import org.battleplugins.arena.event.action.types.GiveEffectsAction;
import org.battleplugins.arena.event.action.types.HealthAction;
import org.battleplugins.arena.event.action.types.KillEntitiesAction;
import org.battleplugins.arena.event.action.types.LeaveAction;
import org.battleplugins.arena.event.action.types.PlaySoundAction;
import org.battleplugins.arena.event.action.types.ResetStateAction;
import org.battleplugins.arena.event.action.types.RespawnAction;
import org.battleplugins.arena.event.action.types.RestoreAction;
import org.battleplugins.arena.event.action.types.RunCommandAction;
import org.battleplugins.arena.event.action.types.SendMessageAction;
import org.battleplugins.arena.event.action.types.StoreAction;
import org.battleplugins.arena.event.action.types.TeleportAction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class EventActionType<T extends EventAction> {
    private static final Map<String, EventActionType<?>> ACTION_TYPES = new HashMap<>();

    public static final EventActionType<ChangeGamemodeAction> CHANGE_GAMEMODE = new EventActionType<>("change-gamemode", ChangeGamemodeAction.class, ChangeGamemodeAction::new);
    public static final EventActionType<ClearEffectsAction> CLEAR_EFFECTS = new EventActionType<>("clear-effects", ClearEffectsAction.class, ClearEffectsAction::new);
    public static final EventActionType<DelayAction> DELAY = new EventActionType<>("delay", DelayAction.class, DelayAction::new);
    public static final EventActionType<FlightAction> FLIGHT = new EventActionType<>("flight", FlightAction.class, FlightAction::new);
    public static final EventActionType<GiveEffectsAction> GIVE_EFFECTS = new EventActionType<>("give-effects", GiveEffectsAction.class, GiveEffectsAction::new);
    public static final EventActionType<HealthAction> HEALTH = new EventActionType<>("health", HealthAction.class, HealthAction::new);
    public static final EventActionType<KillEntitiesAction> KILL_ENTITIES = new EventActionType<>("kill-entities", KillEntitiesAction.class, KillEntitiesAction::new);
    public static final EventActionType<LeaveAction> LEAVE = new EventActionType<>("leave", LeaveAction.class, LeaveAction::new);
    public static final EventActionType<PlaySoundAction> PLAY_SOUND = new EventActionType<>("play-sound", PlaySoundAction.class, PlaySoundAction::new);
    public static final EventActionType<ResetStateAction> RESET_STATE = new EventActionType<>("reset-state", ResetStateAction.class, ResetStateAction::new);
    public static final EventActionType<RespawnAction> RESPAWN = new EventActionType<>("respawn", RespawnAction.class, RespawnAction::new);
    public static final EventActionType<RestoreAction> RESTORE = new EventActionType<>("restore", RestoreAction.class, RestoreAction::new);
    public static final EventActionType<RunCommandAction> RUN_COMMAND = new EventActionType<>("run-command", RunCommandAction.class, RunCommandAction::new);
    public static final EventActionType<SendMessageAction> SEND_MESSAGE = new EventActionType<>("send-message", SendMessageAction.class, SendMessageAction::new);
    public static final EventActionType<StoreAction> STORE = new EventActionType<>("store", StoreAction.class, StoreAction::new);
    public static final EventActionType<TeleportAction> TELEPORT = new EventActionType<>("teleport", TeleportAction.class, TeleportAction::new);

    private final Class<T> clazz;
    private final Function<Map<String, String>, T> factory;

    EventActionType(String name, Class<T> clazz, Function<Map<String, String>, T> factory) {
        this.clazz = clazz;
        this.factory = factory;

        ACTION_TYPES.put(name, this);
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
}
