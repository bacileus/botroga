package org.bacileus.botroga.modules.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;

import org.bacileus.botroga.constants.Emoji;
import org.bacileus.botroga.modules.GenericProcessor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class PlaylistProcessor extends GenericProcessor {
    private static final String PLAY_CMD = "play";
    private static final String TRACK_OPTION = "track";
    private static final String SKIP_CMD = "skip";
    private static final String CLEAR_CMD = "clear";
    private final AudioPlayerManager m_audioPlayerManager;
    private final GuildMusicManager m_guildMusicManager;

    public PlaylistProcessor() {
        super();

        m_supportedCommands.add(Commands.slash(PLAY_CMD, "Reproduces selected track.")
                .addOption(OptionType.STRING, TRACK_OPTION, "URL or keywords to search.", true));
        m_supportedCommands.add(Commands.slash(SKIP_CMD, "Stops reproducing current track and plays next (if any)."));
        m_supportedCommands.add(Commands.slash(CLEAR_CMD, "Stops reproducing current track and resets playlist."));

        m_audioPlayerManager = new DefaultAudioPlayerManager();
        m_guildMusicManager = new GuildMusicManager(m_audioPlayerManager);

        AudioSourceManagers.registerRemoteSources(m_audioPlayerManager);
        AudioSourceManagers.registerLocalSource(m_audioPlayerManager);
    }

    @Override
    public void process(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case PLAY_CMD -> {
                event.reply(Emoji.MUSICAL_NOTE
                        + " **Searching** "
                        + Emoji.MAGNIFYING_GLASS_TILTED_RIGHT).queue();

                processPlay(event);
            }
            case SKIP_CMD -> {
                event.reply(Emoji.FAST_FORWARD_BUTTON
                        + " **Skipping clip** "
                        + Emoji.FAST_FORWARD_BUTTON).queue();

                processSkip(event);
            }
            default -> {
                // Do nothing
            }
        }
    }

    private void processPlay(SlashCommandInteractionEvent event) {
        GuildVoiceState senderGuildVoiceState = event.getMember().getVoiceState();
        TextChannel textChannel = (TextChannel) event.getChannel();

        if (!senderGuildVoiceState.inAudioChannel()) {
            textChannel.sendMessage("** You are not in a voice channel!** "
                    + Emoji.ENRAGED_FACE).queue();
            return;
        }
        if (event.getUser().isBot()) {
            return;
        }

        String resourceMedia = event.getOption(TRACK_OPTION).getAsString();

        if (!isURL(resourceMedia)) {
            resourceMedia = "ytsearch:" + resourceMedia;
        }

        Guild serverGuild = event.getGuild();
        GuildVoiceState botGuildVoiceState = serverGuild.getSelfMember().getVoiceState();

        if (senderGuildVoiceState.getChannel() != botGuildVoiceState.getChannel()) {
            AudioManager guildAudioManager = serverGuild.getAudioManager();
            VoiceChannel guildVoiceChannel = (VoiceChannel) senderGuildVoiceState.getChannel();

            guildAudioManager.openAudioConnection(guildVoiceChannel);
            guildAudioManager.setSendingHandler(m_guildMusicManager.getM_audioPlayerSendHandler());
        }

        playResource(textChannel, resourceMedia);
    }

    private void processSkip(SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        boolean isAudioPlayerPaused = (m_guildMusicManager.getM_audioPlayer().getPlayingTrack() == null);

        if (!m_guildMusicManager.getM_trackScheduler().nextTrack() && isAudioPlayerPaused) {
            event.getChannel().sendMessage("**There is nothing to skip!** "
                    + Emoji.PROHIBITED).queue();
        }
    }

    private void playResource(TextChannel textChannel, String resourceMedia) {
        m_audioPlayerManager.loadItemOrdered(m_guildMusicManager, resourceMedia, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                String cmdResponse;

                if (m_guildMusicManager.getM_trackScheduler().addTrack(track)) {
                    cmdResponse = "**Added to queue **" + Emoji.CHECK_MARK_BUTTON;
                } else {
                    cmdResponse = "**Going to play **" + Emoji.RADIO;
                }

                cmdResponse += " `"
                        + track.getInfo().title
                        + "`\n**URL** "
                        + Emoji.LINK
                        + " "
                        + track.getInfo().uri;
                textChannel.sendMessage(cmdResponse).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> trackList = playlist.getTracks();

                if (!trackList.isEmpty()) {
                    m_guildMusicManager.getM_trackScheduler().addTrack(trackList.get(0));
                } else {
                    noMatches();
                }
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage(Emoji.SLIGHTLY_FROWNING_FACE
                        + " **Song not found** "
                        + Emoji.SLIGHTLY_FROWNING_FACE).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                textChannel.sendMessage(Emoji.POLICE_CAR_LIGHT
                        + " **An exception was raised** "
                        + Emoji.POLICE_CAR_LIGHT
                        + " `"
                        + exception.getMessage()
                        + " `").queue();
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
