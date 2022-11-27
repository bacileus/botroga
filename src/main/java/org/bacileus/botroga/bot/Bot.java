package org.bacileus.botroga.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import org.bacileus.botroga.listeners.PingCmdListener;
import org.bacileus.botroga.listeners.PlaylistCmdListener;

public final class Bot {
    private static final String TOKEN = "ODg3Nzk0Nzk0MjM0MDA3NjEy.YUJVVQ.Qd_nsMGbbmZRCDjwzm1o2jO9kL0";
    private static final String CURRENT_GAME = "AIMLAB";
    private final JDABuilder m_jdaBuilder;

    private Bot() {
        m_jdaBuilder = JDABuilder.createDefault(TOKEN);

        m_jdaBuilder.setStatus(OnlineStatus.ONLINE);
        m_jdaBuilder.setActivity(Activity.playing(CURRENT_GAME));
        addListeners();
    }

    private void addListeners() {
        m_jdaBuilder.addEventListeners(new PingCmdListener());
        m_jdaBuilder.addEventListeners(new PlaylistCmdListener());
    }

    private void build() throws InterruptedException {
        m_jdaBuilder.build().awaitReady();
    }

    public static void main(String[] args) {
        try {
            new Bot().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
