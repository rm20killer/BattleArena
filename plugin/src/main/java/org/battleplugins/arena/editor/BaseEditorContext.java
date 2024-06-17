package org.battleplugins.arena.editor;

import org.battleplugins.arena.Arena;
import org.bukkit.entity.Player;

public class BaseEditorContext extends EditorContext<BaseEditorContext> {
    public BaseEditorContext(ArenaEditorWizard<BaseEditorContext> wizard, Arena arena, Player player) {
        super(wizard, arena, player);
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}