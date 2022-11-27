package org.bacileus.botroga.listeners;

import org.bacileus.botroga.modules.ping.PingProcessor;

public class PingCmdListener extends GenericCmdListener {
    public PingCmdListener() {
        m_cmdProcessor = new PingProcessor();
    }
}
