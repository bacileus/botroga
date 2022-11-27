package org.bacileus.botroga.listeners;

import org.bacileus.botroga.modules.playlist.PlaylistProcessor;

public class PlaylistCmdListener extends GenericCmdListener {
    public PlaylistCmdListener() {
        m_cmdProcessor = new PlaylistProcessor();
    }
}
