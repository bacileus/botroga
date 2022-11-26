package org.bacileus.botroga.listeners;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericCmdListener extends ListenerAdapter {
    protected List<CommandData> supportedCommands;

    public GenericCmdListener() {
        supportedCommands = new ArrayList<>();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        event.getGuild().updateCommands().addCommands(supportedCommands).queue();
    }
}
