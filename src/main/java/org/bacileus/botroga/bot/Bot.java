package org.bacileus.botroga.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bacileus.botroga.listeners.PingCmdListener;

public final class Bot {
    private static final String TOKEN = "ODg3Nzk0Nzk0MjM0MDA3NjEy.YUJVVQ.Qd_nsMGbbmZRCDjwzm1o2jO9kL0";
    private static final String CURRENT_GAME = "AIMLAB";
    private final JDABuilder jdaBuilder;

    private Bot() {
        jdaBuilder = JDABuilder.createDefault(TOKEN);

        jdaBuilder.setStatus(OnlineStatus.ONLINE);
        jdaBuilder.setActivity(Activity.playing(CURRENT_GAME));
        addListeners();
    }

    private void addListeners() {
        jdaBuilder.addEventListeners(new PingCmdListener());
    }

    private void build() throws InterruptedException {
        jdaBuilder.build().awaitReady();
    }

    public static void main(String[] args) {
        try {
            new Bot().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
