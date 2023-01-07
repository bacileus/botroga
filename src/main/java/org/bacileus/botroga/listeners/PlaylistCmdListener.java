package org.bacileus.botroga.listeners;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;

import org.bacileus.botroga.modules.playlist.PlaylistProcessor;

import org.jetbrains.annotations.NotNull;

public class PlaylistCmdListener extends GenericCmdListener {
    public PlaylistCmdListener() {
        m_cmdProcessor = new PlaylistProcessor();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        super.onGuildReady(event);
        ((PlaylistProcessor) m_cmdProcessor).getGuildMusicManager().setServerGuild(event.getGuild());
    }
}
