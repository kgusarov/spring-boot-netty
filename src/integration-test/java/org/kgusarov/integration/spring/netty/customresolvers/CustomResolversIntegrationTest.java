package org.kgusarov.integration.spring.netty.customresolvers;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.customresolvers.resolvers.RNG;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
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
@ContextConfiguration(classes = CustomResolversApplication.class, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomResolversIntegrationTest {
    @Autowired
    private NettyServers servers;

    @Autowired
    private ProcessingCounter counter;

    @Autowired
    private RNG rng;

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
        final SettableFuture<Long> firstRN = SettableFuture.create();
        final SettableFuture<Long> secondRN = SettableFuture.create();

        final int serverPort = servers.get(0).getBoundToPort();

        rng.getGeneratedNumbers().clear();

        final ServerClient client = new ServerClient(
                serverPort,
                "localhost",
                new ClientHandler(firstRN, secondRN)
        );

        client.connect();
        client.writeAndFlush(Unpooled.copyBoolean(true)).syncUninterruptibly();
        Futures.successfulAsList(firstRN, secondRN).get(30, TimeUnit.SECONDS);
        client.disconnect();

        counter.awaitAdvanceInterruptibly(phase, 30, TimeUnit.SECONDS);
        final Long l1 = firstRN.get(30, TimeUnit.SECONDS);
        final Long l2 = secondRN.get(30, TimeUnit.SECONDS);

        assertEquals(3, rng.getGeneratedNumbers().size());
        assertEquals(l1, rng.getGeneratedNumbers().get(0));
        assertEquals(l2, rng.getGeneratedNumbers().get(1));
    }

    private static final class ClientHandler extends ChannelInboundHandlerAdapter {
        private final SettableFuture<Long>[] futures;
        int idx;

        @SafeVarargs
        private ClientHandler(final SettableFuture<Long> ...futures) {
            this.futures = futures;
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
            final ByteBuf bb = (ByteBuf) msg;

            while (bb.isReadable(8)) {
                futures[idx++].set(bb.readLong());
            }
        }
    }
}
