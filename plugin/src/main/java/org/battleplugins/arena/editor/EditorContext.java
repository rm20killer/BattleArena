package org.battleplugins.arena.editor;

import net.kyori.adventure.text.Component;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class EditorContext<E extends EditorContext<E>> {

    protected final ArenaEditorWizard<E> wizard;
    protected final Arena arena;
    protected final Player player;
    private Runnable advanceListener;
    private int position;

    private final List<Listener> boundListeners = new ArrayList<>();

    protected boolean reconstructed;

    public EditorContext(ArenaEditorWizard<E> wizard, Arena arena, Player player) {
        this.wizard = wizard;
        this.arena = arena;
        this.player = player;
    }

    public BattleArena getPlugin() {
        return this.wizard.getPlugin();
    }

    public ArenaEditorWizard<E> getWizard() {
        return this.wizard;
    }

    public Arena getArena() {
        return this.arena;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isReconstructed() {
        return this.reconstructed;
    }

    void setAdvanceListener(Runnable listener) {
        this.advanceListener = listener;
    }

    public void advanceStage() {
        this.position++;
        if (this.advanceListener != null) {
            this.advanceListener.run();
        }
    }

    public void bind(Listener listener) {
        this.boundListeners.add(listener);
    }

    public void unbind(Listener listener) {
        this.boundListeners.remove(listener);
    }

    public void cancel() {
        this.boundListeners.forEach(HandlerList::unregisterAll);
        this.boundListeners.clear();
    }

    public void inform(Message message) {
        if (this.reconstructed) {
            message.send(this.player);
            return;
        }

        Component positionMessage = Component.text("(" + (this.position + 1) + "/" + this.wizard.getStages().size() + ") ", Messages.SECONDARY_COLOR)
                .append(message.toComponent());

        this.player.sendMessage(positionMessage);
    }

    public abstract boolean isComplete();
}
