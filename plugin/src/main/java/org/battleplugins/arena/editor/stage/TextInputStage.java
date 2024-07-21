package org.battleplugins.arena.editor.stage;

import org.battleplugins.arena.editor.EditorContext;
import org.battleplugins.arena.editor.WizardStage;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.util.InteractionInputs;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class TextInputStage<E extends EditorContext<E>> implements WizardStage<E> {
    private final Message chatMessage;
    private final Message invalidInputMessage;
    private final BiFunction<E, String, Boolean> validContentFunction;
    private final Function<E, Consumer<String>> inputConsumer;

    public TextInputStage(Message chatMessage, Message invalidInputMessage, Function<E, Consumer<String>> inputConsumer) {
        this(chatMessage, invalidInputMessage, null, inputConsumer);
    }

    public TextInputStage(Message chatMessage, Message invalidInputMessage, BiFunction<E, String, Boolean> validContentFunction, Function<E, Consumer<String>> inputConsumer) {
        this.chatMessage = chatMessage;
        this.invalidInputMessage = invalidInputMessage;
        this.validContentFunction = validContentFunction;
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void enter(E context) {
        if (this.chatMessage != null) {
            context.inform(this.chatMessage);
        }

        new InteractionInputs.ChatInput(context.getPlayer(), this.invalidInputMessage) {

            @Override
            public void onChatInput(String input) {
                inputConsumer.apply(context).accept(input);
                context.advanceStage();
            }

            @Override
            public boolean isValidChatInput(String input) {
                return super.isValidChatInput(input) && (!input.startsWith("/") && (validContentFunction == null || validContentFunction.apply(context, input)));
            }
        }.bind(context);
    }
}