package org.bacileus.botroga.modules.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final GuildMusicManager m_guildMusicManager;
    private final Queue<AudioTrack> m_trackQueue;

    public TrackScheduler(GuildMusicManager guildMusicManager) {
        m_guildMusicManager = guildMusicManager;
        m_trackQueue = new ConcurrentLinkedQueue<>();
    }

    public void clear() {
        AudioPlayer audioPlayer = m_guildMusicManager.getAudioPlayer();

        if (audioPlayer.getPlayingTrack() != null) {
            audioPlayer.stopTrack();
        }

        m_trackQueue.clear();
    }

    public boolean addTrack(AudioTrack track) {
        m_guildMusicManager.stopAudioDisconnectionScheduler();
        AudioPlayer audioPlayer = m_guildMusicManager.getAudioPlayer();
        boolean isTrackStarted = audioPlayer.startTrack(track, true);

        if (!isTrackStarted) {
            m_trackQueue.add(track);
        }

        return !isTrackStarted;
    }

    public boolean nextTrack() {
        AudioPlayer audioPlayer = m_guildMusicManager.getAudioPlayer();
        boolean isTrackStarted = audioPlayer.startTrack(m_trackQueue.poll(), false);

        if (!isTrackStarted) {
            m_guildMusicManager.startAudioDisconnectionScheduler();
        }

        return isTrackStarted;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
