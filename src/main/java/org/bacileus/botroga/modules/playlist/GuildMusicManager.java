package org.bacileus.botroga.modules.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager {
    private static final int NUM_SCHEDULED_THREADS = 1;
    private static final int DISCONNECT_IN_MINUTES = 10;
    private final AudioPlayer m_audioPlayer;
    private final TrackScheduler m_trackScheduler;
    private final AudioPlayerSendHandler m_audioPlayerSendHandler;
    private final ScheduledExecutorService m_scheduledExecutorService;
    private Guild m_serverGuild;
    private ScheduledFuture<?> m_disconnectTask;

    public GuildMusicManager(AudioPlayerManager audioPlayerManager) {
        m_audioPlayer = audioPlayerManager.createPlayer();
        m_trackScheduler = new TrackScheduler(this);
        m_audioPlayerSendHandler = new AudioPlayerSendHandler(m_audioPlayer);
        m_scheduledExecutorService = new ScheduledThreadPoolExecutor(NUM_SCHEDULED_THREADS);

        m_audioPlayer.addListener(m_trackScheduler);
    }

    public AudioPlayer getAudioPlayer() {
        return m_audioPlayer;
    }

    public TrackScheduler getTrackScheduler() {
        return m_trackScheduler;
    }

    public AudioPlayerSendHandler getAudioPlayerSendHandler() {
        return m_audioPlayerSendHandler;
    }

    public void setServerGuild(Guild serverGuild) {
        m_serverGuild = serverGuild;
    }

    public void startAudioDisconnectionScheduler() {
        m_disconnectTask = m_scheduledExecutorService.schedule(
                () -> m_serverGuild.getAudioManager().closeAudioConnection(),
                DISCONNECT_IN_MINUTES,
                TimeUnit.MINUTES);
    }

    public void stopAudioDisconnectionScheduler() {
        if (m_disconnectTask != null) {
            m_disconnectTask.cancel(true);
            m_disconnectTask = null;
        }
    }
}
