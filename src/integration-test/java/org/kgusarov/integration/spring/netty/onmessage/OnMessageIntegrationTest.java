package org.kgusarov.integration.spring.netty.onmessage;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.kgusarov.integration.spring.netty.onmessage.handlers.Decoder.DECODE;
import static org.kgusarov.integration.spring.netty.onmessage.handlers.Encoder.ENCODE;
import static org.kgusarov.integration.spring.netty.onmessage.handlers.OnMessageController.*;
import static org.kgusarov.integration.spring.netty.onmessage.handlers.TransactionalOnMessageController.TRANSACTIONAL_ON_MESSAGE;

@ActiveProfiles("onmessage")
@SpringBootTest
@ContextConfiguration(classes = {
        OnMessageApplication.class,
        HandlerMethodCalls.class}, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class OnMessageIntegrationTest {
    @Autowired
    private NettyServers servers;

    @Autowired
    private HandlerMethodCalls calls;

    @Autowired
    private ProcessingCounter counter;

    @Test
    @DirtiesContext
    public void testServersShouldBePresent() {
        assertThat(servers, not(hasSize(0)));
    }

    @Test
    @DirtiesContext
    public void testMessageHandlersWork() throws Exception {
        runTestOnce(0);
        runTestOnce(1);
    }

    private void runTestOnce(final int phase) throws InterruptedException, TimeoutException, ExecutionException {
        calls.clear();

        final SettableFuture<Object> msgPlus1Future = SettableFuture.create();
        final SettableFuture<Object> msgPlus2Future = SettableFuture.create();
        final SettableFuture<Object> msgFuture = SettableFuture.create();
        final SettableFuture<Object> l889Future1 = SettableFuture.create();
        final SettableFuture<Object> l26576374Future1 = SettableFuture.create();
        final SettableFuture<Object> helloWorldFuture = SettableFuture.create();
        final SettableFuture<Object> l889Future2 = SettableFuture.create();
        final SettableFuture<Object> l26576374Future2 = SettableFuture.create();

        final ServerClient client = new ServerClient(
                40000,
                "localhost",
                new Decoder(),
                new Encoder(),
                new ClientHandler(msgPlus1Future, msgPlus2Future, msgFuture, l889Future1, l26576374Future1,
                        helloWorldFuture, l889Future2, l26576374Future2)
        );

        client.connect();

        client.writeAndFlush(100500L).syncUninterruptibly();
        client.writeAndFlush("Hello, world!").syncUninterruptibly();

        counter.awaitAdvanceInterruptibly(phase, 30, TimeUnit.SECONDS);
        assertEquals(100501L, msgPlus1Future.get(30, TimeUnit.SECONDS));
        assertEquals(100502L, msgPlus2Future.get(30, TimeUnit.SECONDS));
        assertEquals(100500L, msgFuture.get(30, TimeUnit.SECONDS));
        assertEquals(889L, l889Future1.get(30, TimeUnit.SECONDS));
        assertEquals(26576374L, l26576374Future1.get(30, TimeUnit.SECONDS));
        assertEquals("Hello, world!", helloWorldFuture.get(30, TimeUnit.SECONDS));
        assertEquals(889L, l889Future2.get(30, TimeUnit.SECONDS));
        assertEquals(26576374L, l26576374Future2.get(30, TimeUnit.SECONDS));

        client.disconnect();

        assertThat(calls, contains(
                DECODE,
                ON_MESSAGE1,
                ENCODE,
                ENCODE,
                ON_MESSAGE2,
                ENCODE,
                ON_MESSAGE3,
                TRANSACTIONAL_ON_MESSAGE,
                ENCODE,
                ENCODE,

                DECODE,
                ON_STRING_MESSAGE,
                ENCODE,
                ON_MESSAGE3,
                TRANSACTIONAL_ON_MESSAGE,
                ENCODE,
                ENCODE
        ));
    }

    private static final class ClientHandler extends ChannelInboundHandlerAdapter {
        private final SettableFuture<Object>[] futures;
        int idx;

        @SafeVarargs
        private ClientHandler(final SettableFuture<Object> ...futures) {
            this.futures = futures;
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
            futures[idx++].set(msg);
        }
    }
}
