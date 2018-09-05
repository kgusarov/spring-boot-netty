package org.kgusarov.integration.spring.netty.onmessage;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.onmessage.handlers.Decoder;
import org.kgusarov.integration.spring.netty.onmessage.handlers.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@ActiveProfiles("onmessage")
@SpringBootTest
@ContextConfiguration(classes = OnMessageApplication.class, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class OnMessageIntegrationTest {
    @Autowired
    private NettyServers servers;

    @Test
    @DirtiesContext
    public void testServersShouldBePresent() {
        assertThat(servers, not(hasSize(0)));
    }

    @Test
    @DirtiesContext
    public void testMessageHandlersWork() throws Exception {
        runTestOnce();
        runTestOnce();
    }

    private void runTestOnce() throws InterruptedException, TimeoutException, ExecutionException {
        final SettableFuture<String> strFuture = SettableFuture.create();
        final SettableFuture<Long> longFuture = SettableFuture.create();

        final ServerClient client = new ServerClient(
                40000,
                "localhost",
                new Decoder(),
                new Encoder(),
                new ClientHandler(strFuture, longFuture)
        );

        client.connect();
        client.writeAndFlush("Hello, world!").syncUninterruptibly();

        final String rs = strFuture.get(30, TimeUnit.SECONDS);
        assertEquals("Hello, world!", rs);

        client.writeAndFlush(100500L).syncUninterruptibly();
        final long rl = longFuture.get(30, TimeUnit.SECONDS);
        assertEquals(100500L, rl);

        client.disconnect();
    }

    private static final class ClientHandler extends ChannelInboundHandlerAdapter {
        private final SettableFuture<String> strFuture;
        private final SettableFuture<Long> longFuture;

        private ClientHandler(final SettableFuture<String> strFuture, final SettableFuture<Long> longFuture) {
            this.strFuture = strFuture;
            this.longFuture = longFuture;
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            if (msg instanceof String) {
                strFuture.set((String) msg);
            } else if (msg instanceof Long) {
                longFuture.set((Long) msg);
            }
        }
    }
}
