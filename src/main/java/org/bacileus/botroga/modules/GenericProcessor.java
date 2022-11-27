package org.bacileus.botroga.modules;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericProcessor {
    protected List<CommandData> m_supportedCommands;

    public GenericProcessor() {
        m_supportedCommands = new ArrayList<>();
    }

    public List<CommandData> getM_supportedCommands() {
        return m_supportedCommands;
    }

    public abstract void process(SlashCommandInteractionEvent event);
}
