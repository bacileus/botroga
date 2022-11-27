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

    public boolean addTrack(AudioTrack track) {
        if (!m_audioPlayer.startTrack(track, true)) {
            m_blockingQueue.add(track);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            m_audioPlayer.startTrack(m_blockingQueue.poll(), false);
        }
    }
}
