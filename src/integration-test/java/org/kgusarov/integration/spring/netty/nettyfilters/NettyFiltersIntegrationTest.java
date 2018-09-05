package org.kgusarov.integration.spring.netty.nettyfilters;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.buffer.ByteBuf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.etc.ClientHandler;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.kgusarov.integration.spring.netty.nettyfilters.handlers.*;
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

@ActiveProfiles("filters")
@SpringBootTest
@ContextConfiguration(classes = {
        NettyFiltersApplication.class,
        HandlerCallStack.class
}, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class NettyFiltersIntegrationTest {

    @Autowired
    private NettyServers servers;

    @Autowired
    private HandlerCallStack handlerCallStack;

    @Test
    @DirtiesContext
    public void testServersShouldBePresent() {
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

        //noinspection unchecked
        assertThat(handlerCallStack, contains(
            LongDecoder.class,
            LongInverter.class,
            AroundResponderFilter.class,
            LongResponder.class,
            AroundResponderFilter.class,
            LongEncoder.class,

            LongDecoder.class,
            LongInverter.class,
            AroundResponderFilter.class,
            LongResponder.class,
            AroundResponderFilter.class,
            LongEncoder.class
        ));
    }
}
