package org.bacileus.botroga.modules.playlist;

import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {
    private static final int BYTE_BUFFER_CAPACITY = 1024;
    private final GuildMusicManager m_guildMusicManager;
    private final ByteBuffer m_byteBuffer;
    private final MutableAudioFrame m_mutableAudioFrame;

    public AudioPlayerSendHandler(GuildMusicManager guildMusicManager) {
        m_guildMusicManager = guildMusicManager;
        m_byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY);
        m_mutableAudioFrame = new MutableAudioFrame();

        m_mutableAudioFrame.setBuffer(m_byteBuffer);
    }

    @Override
    public boolean canProvide() {
        return m_guildMusicManager.getAudioPlayer().provide(m_mutableAudioFrame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return m_byteBuffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
