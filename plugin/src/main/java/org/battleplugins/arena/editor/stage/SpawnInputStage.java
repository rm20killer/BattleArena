package org.battleplugins.arena.editor.stage;

import org.battleplugins.arena.editor.EditorContext;
import org.battleplugins.arena.editor.WizardStage;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.util.InteractionInputs;
import org.bukkit.Location;

import java.util.function.Consumer;
import java.util.function.Function;

public class SpawnInputStage<E extends EditorContext<E>> implements WizardStage<E> {
    private final Message chatMessage;
    private final String input;
    private final Function<E, Consumer<Location>> inputConsumer;

    public SpawnInputStage(Message chatMessage, String input, Function<E, Consumer<Location>> inputConsumer) {
        this.chatMessage = chatMessage;
        this.input = input;
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void enter(E context) {
        if (this.chatMessage != null) {
            context.inform(this.chatMessage);
        }

        // Player types in chat their location is used for the spawn
        new InteractionInputs.ChatInput(context.getPlayer(), null) {

            @Override
            public void onChatInput(String input) {
                inputConsumer.apply(context).accept(context.getPlayer().getLocation());
                context.advanceStage();
            }

            @Override
            public boolean isValidChatInput(String input) {
                return super.isValidChatInput(input) && !input.startsWith("/") && SpawnInputStage.this.input.equalsIgnoreCase(input);
            }
        }.bind(context);
    }
}