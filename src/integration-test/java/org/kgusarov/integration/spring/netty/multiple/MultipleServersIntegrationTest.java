package org.kgusarov.integration.spring.netty.multiple;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
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

import static io.netty.buffer.Unpooled.copiedBuffer;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("multiple")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = MultipleServersApplication.class, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleServersIntegrationTest {
    @Autowired
    private NettyServers servers;

    @Test
    @DirtiesContext
    public void testServersShouldBePresent() throws Exception {
        assertThat(servers, hasSize(2));
    }

    @Test
    @DirtiesContext
    public void testMultipleServersWork() throws Exception {
        final String s1 = connectAndGetResponse(40000);
        final String s2 = connectAndGetResponse(40001);

        assertEquals("Hello, world!", s1);
        assertEquals("48656c6c6f2c20776f726c6421", s2);
    }

    private String connectAndGetResponse(final int port) throws InterruptedException, TimeoutException, ExecutionException {
        final SettableFuture<String> strFuture = SettableFuture.create();
        final ServerClient client = new ServerClient(port, "localhost", new ClientHandler(strFuture));
        final ByteBuf request = copiedBuffer("Hello, world!", CharsetUtil.UTF_8);

        client.connect().get().writeAndFlush(request).syncUninterruptibly();
        final String response = strFuture.get(30, TimeUnit.SECONDS);
        client.disconnect();

        return response;
    }

    private static class ClientHandler extends ChannelInboundHandlerAdapter {
        private final SettableFuture<String> strFuture;

        private ClientHandler(final SettableFuture<String> strFuture) {
            this.strFuture = strFuture;
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            final ByteBuf byteBuf = (ByteBuf) msg;
            final byte[] bytes = ByteBufUtil.getBytes(byteBuf);
            final String str = new String(bytes, CharsetUtil.UTF_8);

            strFuture.set(str);
        }
    }
}
