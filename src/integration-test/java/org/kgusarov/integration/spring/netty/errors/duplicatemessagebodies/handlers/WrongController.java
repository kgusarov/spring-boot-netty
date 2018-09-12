package org.kgusarov.integration.spring.netty.errors.duplicatemessagebodies.handlers;

import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyMessageBody;
import org.kgusarov.integration.spring.netty.annotations.NettyOnMessage;

@NettyController
public class WrongController {
    @NettyOnMessage(serverName = "server1")
    public void onConnect(@NettyMessageBody final int body1, @NettyMessageBody final int body2) {
    }
}
