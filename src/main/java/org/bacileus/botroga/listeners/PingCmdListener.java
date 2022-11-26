package org.bacileus.botroga.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bacileus.botroga.modules.GenericProcessor;
import org.bacileus.botroga.modules.ping.PingProcessor;

import org.jetbrains.annotations.NotNull;

public class PingCmdListener extends GenericCmdListener {
    private final GenericProcessor pingProcessor;

    public PingCmdListener() {
        super();
        supportedCommands.add(Commands.slash("ping", "Bot responds pong"));

        pingProcessor = new PingProcessor();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        pingProcessor.process(event);
    }
}
