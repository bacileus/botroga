package org.bacileus.botroga.modules.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer m_audioPlayer;
    private final Queue<AudioTrack> m_blockingQueue;

    public TrackScheduler(AudioPlayer audioPlayer) {
        m_audioPlayer = audioPlayer;
        m_blockingQueue = new ConcurrentLinkedQueue<>();
    }

    public void clear() {
        if (m_audioPlayer.getPlayingTrack() != null) {
            m_audioPlayer.stopTrack();
        }

        m_blockingQueue.clear();
    }

    public boolean addTrack(AudioTrack track) {
        boolean isTrackStarted = m_audioPlayer.startTrack(track, true);
        if (!isTrackStarted) {
            m_blockingQueue.add(track);
        }

        return !isTrackStarted;
    }

    public boolean nextTrack() {
        return m_audioPlayer.startTrack(m_blockingQueue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
