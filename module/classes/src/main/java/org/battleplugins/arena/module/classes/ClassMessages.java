package org.battleplugins.arena.module.classes;

import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;

public final class ClassMessages {

    public static final Message CLASSES_NOT_ENABLED = Messages.error("classes-not-enabled", "Classes are not enabled in this arena.");
    public static final Message CLASS_NOT_FOUND = Messages.error("classes-class-not-found", "The class could not be found.");
    public static final Message CANNOT_EQUIP_CLASS = Messages.error("classes-cannot-equip-class", "You cannot equip a class at this time.");
    public static final Message CLASS_EQUIPPED = Messages.success("classes-class-equipped", "You have equipped the class <secondary>{}</secondary>.");
}
