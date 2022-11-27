package org.bacileus.botroga.modules.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
    private final AudioPlayer m_audioPlayer;
    private final TrackScheduler m_trackScheduler;
    private final AudioPlayerSendHandler m_audioPlayerSendHandler;

    public GuildMusicManager(AudioPlayerManager audioPlayerManager) {
        m_audioPlayer = audioPlayerManager.createPlayer();
        m_trackScheduler = new TrackScheduler(m_audioPlayer);
        m_audioPlayerSendHandler = new AudioPlayerSendHandler(m_audioPlayer);

        m_audioPlayer.addListener(m_trackScheduler);
    }

    public AudioPlayer getM_audioPlayer() {
        return m_audioPlayer;
    }

    public TrackScheduler getM_trackScheduler() {
        return m_trackScheduler;
    }

    public AudioPlayerSendHandler getM_audioPlayerSendHandler() {
        return m_audioPlayerSendHandler;
    }
}
