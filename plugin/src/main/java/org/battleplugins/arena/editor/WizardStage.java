package org.battleplugins.arena.editor;

public interface WizardStage<E extends EditorContext<E>> {

    void enter(E context);
}