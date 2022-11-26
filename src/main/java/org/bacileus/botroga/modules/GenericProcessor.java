package org.bacileus.botroga.modules;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public abstract class GenericProcessor {
    public abstract void process(GenericCommandInteractionEvent event);
}
