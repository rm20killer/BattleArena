package org.battleplugins.arena.editor;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.BattleArena;
import org.bukkit.entity.Player;

public abstract class EditorContext<E extends EditorContext<E>> {

    protected final ArenaEditorWizard<E> wizard;
    protected final Arena arena;
    protected final Player player;
    private Runnable advanceListener;

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
        if (this.advanceListener != null) {
            this.advanceListener.run();
        }
    }

    public abstract boolean isComplete();
}
