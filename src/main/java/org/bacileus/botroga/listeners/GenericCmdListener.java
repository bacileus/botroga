package org.bacileus.botroga.listeners;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import org.bacileus.botroga.modules.GenericProcessor;

import org.jetbrains.annotations.NotNull;

public abstract class GenericCmdListener extends ListenerAdapter {
    protected GenericProcessor m_cmdProcessor;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        m_cmdProcessor.process(event);
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        for (CommandData supportedCommand : m_cmdProcessor.getM_supportedCommands()) {
            event.getGuild().upsertCommand(supportedCommand).queue();
        }
    }
}
