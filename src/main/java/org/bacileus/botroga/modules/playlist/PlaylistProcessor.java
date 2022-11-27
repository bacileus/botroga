package org.bacileus.botroga.modules.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;

import org.bacileus.botroga.modules.GenericProcessor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class PlaylistProcessor extends GenericProcessor {
    private static final String PLAY_CMD = "play";
    private static final String RESOURCE_MEDIA_OPTION = "resource_media";
    private static final String SKIP_CMD = "skip";
    private static final String CLEAR_CMD = "clear";
    private final AudioPlayerManager m_audioPlayerManager;
    private final GuildMusicManager m_guildMusicManager;

    public PlaylistProcessor() {
        super();

        m_supportedCommands.add(Commands.slash(PLAY_CMD, "Reproduces selected track.")
                .addOption(OptionType.STRING, RESOURCE_MEDIA_OPTION, "URL or keywords to search.", true));
        m_supportedCommands.add(Commands.slash(SKIP_CMD, "Stops reproducing current track and plays next (if any)."));
        m_supportedCommands.add(Commands.slash(CLEAR_CMD, "Stops reproducing current track and resets playlist."));

        m_audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(m_audioPlayerManager);
        AudioSourceManagers.registerLocalSource(m_audioPlayerManager);

        m_guildMusicManager = new GuildMusicManager(m_audioPlayerManager);
    }

    @Override
    public void process(GenericCommandInteractionEvent event) {
        switch (event.getName()) {
            case PLAY_CMD -> {
                event.reply("Processing command...").queue();
                processPlay((SlashCommandInteractionEvent) event);
            }
            default -> {
                // Do nothing
            }
        }
    }

    private void processPlay(SlashCommandInteractionEvent event) {
        GuildVoiceState senderGuildVoiceState = event.getMember().getVoiceState();
        if (!senderGuildVoiceState.inAudioChannel() || event.getUser().isBot()) {
            return;
        }

        String resourceMedia = event.getOption(RESOURCE_MEDIA_OPTION).getAsString();
        if (!isURL(resourceMedia)) {
            resourceMedia = "ytsearch:" + resourceMedia;
        }

        GuildVoiceState botGuildVoiceState = event.getGuild().getSelfMember().getVoiceState();
        if (senderGuildVoiceState.getChannel() != botGuildVoiceState.getChannel()) {
            AudioManager guildAudioManager = event.getGuild().getAudioManager();
            VoiceChannel guildVoiceChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            guildAudioManager.openAudioConnection(guildVoiceChannel);
            guildAudioManager.setSendingHandler(m_guildMusicManager.getM_audioPlayerSendHandler());
        }

        playResource(resourceMedia);
    }

    private void playResource(String resourceMedia) {
        m_audioPlayerManager.loadItemOrdered(m_guildMusicManager, resourceMedia, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                m_guildMusicManager.getM_trackScheduler().queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> trackList = playlist.getTracks();

                if (!trackList.isEmpty()) {
                    m_guildMusicManager.getM_trackScheduler().queue(trackList.get(0));
                } else {
                    noMatches();
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }

    private boolean isURL(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
