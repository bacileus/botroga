package org.bacileus.botroga.modules.ping;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bacileus.botroga.modules.GenericProcessor;

public class PingProcessor extends GenericProcessor {
    private static final String PING_CMD = "ping";

    public PingProcessor() {
        super();

        m_supportedCommands.add(Commands.slash(PING_CMD, "Bot responds pong."));
    }

    @Override
    public void process(GenericCommandInteractionEvent event) {
        if (event.getName().equals(PING_CMD)) {
            event.reply("Pong!").queue();
        }
        // else: Do nothing
    }
}
