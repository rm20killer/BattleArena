package org.battleplugins.arena.editor;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.editor.type.EditorKey;
import org.battleplugins.arena.messages.Messages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ArenaEditorWizard<E extends EditorContext<E>> {

    private final BattleArena plugin;
    private final ContextFactory<E> contextFactory;

    private final Map<String, WizardStage<E>> stages = new LinkedHashMap<>();

    private Consumer<E> onEditComplete;
    private Consumer<E> onCreationComplete;
    private Consumer<E> onCancel;

    private final List<UUID> players = new ArrayList<>();

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

        this.players.remove(context.getPlayer().getUniqueId());
    }

    public void openWizard(Player player, Arena arena) {
        if (this.players.contains(player.getUniqueId())) {
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

                this.players.remove(player.getUniqueId());
            } else {
                WizardStage<E> stage = iterator.next().getValue();
                stage.enter(context);
            }
        });

        WizardStage<E> initialStage = iterator.next().getValue();
        initialStage.enter(context);

        this.players.add(player.getUniqueId());
    }

    public void openSingleWizardStage(Player player, Arena arena, WizardStage<E> stage, Consumer<E> contextConsumer) {
        if (this.players.contains(player.getUniqueId())) {
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

            this.players.remove(player.getUniqueId());
        });

        stage.enter(context);
    }

    @Nullable
    public WizardStage<E> getStage(String key) {
        return this.stages.get(key);
    }

    @Nullable
    public WizardStage<E> getStage(EditorKey key) {
        return this.stages.get(key.getKey());
    }

    public interface ContextFactory<E extends EditorContext<E>> {

        E create(ArenaEditorWizard<E> wizard, Arena arena, Player player);
    }
}
