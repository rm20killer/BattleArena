package org.battleplugins.arena.editor;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.editor.type.EditorKey;
import org.battleplugins.arena.messages.Messages;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ArenaEditorWizard<E extends EditorContext<E>> {
    private static final String EDITOR_META_KEY = "editor";

    private final BattleArena plugin;
    private final ContextFactory<E> contextFactory;

    private final Map<String, WizardStage<E>> stages = new LinkedHashMap<>();

    private Consumer<E> onEditComplete;
    private Consumer<E> onCreationComplete;
    private Consumer<E> onCancel;

    public ArenaEditorWizard(BattleArena plugin, ContextFactory<E> contextFactory) {
        this.plugin = plugin;
        this.contextFactory = contextFactory;
    }

    BattleArena getPlugin() {
        return this.plugin;
    }

    public ArenaEditorWizard<E> addStage(EditorKey key, WizardStage<E> stage) {
        this.stages.put(key.getKey(), stage);
        return this;
    }

    Map<String, WizardStage<E>> getStages() {
        return this.stages;
    }

    public ArenaEditorWizard<E> onEditComplete(Consumer<E> onEditComplete) {
        this.onEditComplete = onEditComplete;
        return this;
    }

    public ArenaEditorWizard<E> onCreationComplete(Consumer<E> onCreationComplete) {
        this.onCreationComplete = onCreationComplete;
        return this;
    }

    public ArenaEditorWizard<E> onCancel(Consumer<E> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public void onCancel(E context) {
        if (this.onCancel != null) {
            this.onCancel.accept(context);
        }

        context.cancel();
        context.getPlayer().removeMetadata(EDITOR_META_KEY, this.plugin);

        Messages.WIZARD_CLOSED.send(context.getPlayer());
    }

    public void openWizard(Player player, Arena arena) {
        this.openWizard(player, arena, null);
    }

    public void openWizard(Player player, Arena arena, @Nullable Consumer<E> contextConsumer) {
        if (player.hasMetadata(EDITOR_META_KEY)) {
            Messages.ERROR_ALREADY_IN_EDITOR.send(player);
            return;
        }

        if (this.stages.isEmpty()) {
            this.plugin.warn("No stages have been added to wizard {}!", this.getClass().getSimpleName());
            return;
        }

        // Close any open inventories
        player.closeInventory();

        E context = this.contextFactory.create(this, arena, player);
        Iterator<Map.Entry<String, WizardStage<E>>> iterator = this.stages.entrySet().iterator();
        context.setAdvanceListener(() -> {
            if (!iterator.hasNext()) {
                if (!context.isComplete()) {
                    Messages.ERROR_OCCURRED_APPLYING_CHANGES.send(player);
                    return;
                }

                if (this.onCreationComplete != null) {
                    this.onCreationComplete.accept(context);
                }

                player.removeMetadata(EDITOR_META_KEY, this.plugin);
            } else {
                WizardStage<E> stage = iterator.next().getValue();
                stage.enter(context);
            }
        });

        if (contextConsumer != null) {
            contextConsumer.accept(context);
        }

        Messages.ENTERED_WIZARD.send(player);

        WizardStage<E> initialStage = iterator.next().getValue();
        initialStage.enter(context);

        player.setMetadata(EDITOR_META_KEY, new FixedMetadataValue(this.plugin, context));
    }

    public void openSingleWizardStage(Player player, Arena arena, WizardStage<E> stage, Consumer<E> contextConsumer) {
        if (player.hasMetadata(EDITOR_META_KEY)) {
            Messages.ERROR_ALREADY_IN_EDITOR.send(player);
            return;
        }

        E context = this.contextFactory.create(this, arena, player);
        contextConsumer.accept(context);

        context.setAdvanceListener(() -> {
            if (!context.isComplete()) {
                Messages.ERROR_OCCURRED_APPLYING_CHANGES.send(player);
                return;
            }

            if (this.onEditComplete != null) {
                this.onEditComplete.accept(context);
            }

            player.removeMetadata(EDITOR_META_KEY, this.plugin);
        });

        stage.enter(context);

        player.setMetadata(EDITOR_META_KEY, new FixedMetadataValue(this.plugin, context));
    }

    @Nullable
    public WizardStage<E> getStage(String key) {
        return this.stages.get(key);
    }

    @Nullable
    public WizardStage<E> getStage(EditorKey key) {
        return this.stages.get(key.getKey());
    }

    public static boolean inWizard(Player player) {
        return player.hasMetadata(EDITOR_META_KEY);
    }

    public static <E extends EditorContext<E>> Optional<E> wizardContext(Player player) {
        return Optional.ofNullable(getWizardContext(player));
    }

    @SuppressWarnings("unchecked")
    public static <E extends EditorContext<E>> E getWizardContext(Player player) {
        return player.hasMetadata(EDITOR_META_KEY) ? (E) player.getMetadata(EDITOR_META_KEY).get(0).value() : null;
    }

    public interface ContextFactory<E extends EditorContext<E>> {

        E create(ArenaEditorWizard<E> wizard, Arena arena, Player player);
    }
}
