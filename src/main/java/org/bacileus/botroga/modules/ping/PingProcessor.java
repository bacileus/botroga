package org.bacileus.botroga.modules.ping;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.bacileus.botroga.modules.GenericProcessor;

public class PingProcessor extends GenericProcessor {
    public PingProcessor() {

    }

    @Override
    public void process(GenericCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }
}
