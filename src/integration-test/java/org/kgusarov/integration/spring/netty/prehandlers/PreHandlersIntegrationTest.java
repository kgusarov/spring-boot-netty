package org.kgusarov.integration.spring.netty.prehandlers;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.buffer.ByteBuf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.etc.ClientHandler;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.kgusarov.integration.spring.netty.prehandlers.handlers.LongDecoder;
import org.kgusarov.integration.spring.netty.prehandlers.handlers.LongEncoder;
import org.kgusarov.integration.spring.netty.prehandlers.handlers.LongInverter;
import org.kgusarov.integration.spring.netty.prehandlers.handlers.LongResponder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.netty.buffer.Unpooled.copyLong;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("prehandlers")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = {
        PreHandlersApplication.class,
        HandlerCallStack.class
}, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class PreHandlersIntegrationTest {

    @Autowired
    private NettyServers servers;

    @Autowired
    private HandlerCallStack handlerCallStack;

    @Test
    @DirtiesContext
    public void testServersShouldBePresent() throws Exception {
        assertThat(servers, not(hasSize(0)));
    }

    @Test
    @DirtiesContext
    public void testHandlersAddedInCorrectOrder() throws Exception {
        final SettableFuture<Long> responseHolder1 = SettableFuture.create();
        final SettableFuture<Long> responseHolder2 = SettableFuture.create();
        final ServerClient client = new ServerClient(40000, "localhost",
                new ClientHandler(responseHolder1, responseHolder2));

        client.connect().get();

        final ByteBuf msg1 = copyLong(1L);
        final ByteBuf msg2 = copyLong(2L);
        client.writeAndFlush(msg1).syncUninterruptibly();
        client.writeAndFlush(msg2).syncUninterruptibly();

        final long actual1 = responseHolder1.get();
        final long actual2 = responseHolder2.get();
        assertEquals(-1L, actual1);
        assertEquals(-2L, actual2);

        client.disconnect();

        assertEquals(8, handlerCallStack.size());

        assertThat(handlerCallStack.get(0), is(equalTo(LongDecoder.class)));
        assertThat(handlerCallStack.get(1), is(equalTo(LongInverter.class)));
        assertThat(handlerCallStack.get(2), is(equalTo(LongResponder.class)));
        assertThat(handlerCallStack.get(3), is(equalTo(LongEncoder.class)));

        assertThat(handlerCallStack.get(4), is(equalTo(LongDecoder.class)));
        assertThat(handlerCallStack.get(5), is(equalTo(LongInverter.class)));
        assertThat(handlerCallStack.get(6), is(equalTo(LongResponder.class)));
        assertThat(handlerCallStack.get(7), is(equalTo(LongEncoder.class)));
    }
}
