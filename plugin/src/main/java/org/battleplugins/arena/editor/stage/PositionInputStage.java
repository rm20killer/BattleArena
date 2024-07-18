package org.battleplugins.arena.editor.stage;

import org.battleplugins.arena.editor.EditorContext;
import org.battleplugins.arena.editor.WizardStage;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.util.InteractionInputs;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class PositionInputStage<E extends EditorContext<E>> implements WizardStage<E> {
    private final Message chatMessage;
    private final Function<E, Consumer<Location>> inputConsumer;

    public PositionInputStage(Message chatMessage, Function<E, Consumer<Location>> inputConsumer) {
        this.chatMessage = chatMessage;
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void enter(E context) {
        if (this.chatMessage != null) {
            context.inform(this.chatMessage);
        }

        new InteractionInputs.PositionInput(context.getPlayer()) {

            @Override
            public void onPositionInteract(Location position) {
                inputConsumer.apply(context).accept(position);
                context.advanceStage();
            }
        }.bind(context);
    }
}