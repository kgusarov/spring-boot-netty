package org.kgusarov.integration.spring.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static org.kgusarov.integration.spring.netty.handlers.FlashPolicyHandler.POLICY_FILE_REQUEST;
import static org.kgusarov.integration.spring.netty.handlers.FlashPolicyHandler.POLICY_FILE_RESPONSE;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FlashPolicyHandlerTest {
    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private ChannelFuture channelFuture;

    @Mock
    private ChannelPipeline channelPipeline;

    private final FlashPolicyHandler handler = new FlashPolicyHandler();

    @Before
    public void setUp() {
        when(channelFuture.addListener(ChannelFutureListener.CLOSE)).thenReturn(channelFuture);
        when(ctx.writeAndFlush(any(ByteBuf.class))).thenReturn(channelFuture);
        when(ctx.pipeline()).thenReturn(channelPipeline);
        when(ctx.fireChannelRead(any())).thenReturn(ctx);
        when(channelPipeline.remove(handler)).thenReturn(channelPipeline);
    }

    @Test
    public void testPolicyIsSentAndConnectionIsClosed() throws Exception {
        final ByteBuf request = copiedBuffer(POLICY_FILE_REQUEST, CharsetUtil.UTF_8);
        final ByteBuf expectedResponse = copiedBuffer(copiedBuffer(POLICY_FILE_RESPONSE, CharsetUtil.UTF_8));
        handler.channelRead(ctx, request);

        verifyNoInteractions(channelPipeline);
        verify(ctx, times(1)).writeAndFlush(
                argThat(arg -> {
                    final ByteBuf bb = (ByteBuf) arg;
                    final byte[] bytesSent = bb.array();
                    final byte[] bytesExpected = expectedResponse.array();

                    return Arrays.equals(bytesExpected, bytesSent);
                })
        );
        verify(channelFuture, times(1)).addListener(ChannelFutureListener.CLOSE);
        verify(ctx, times(0)).fireChannelRead(any(ByteBuf.class));
    }

    @Test
    public void testHandlerIsRemovedIfNonPolicyRequestComesIn() throws Exception {
        final ByteBuf request = copiedBuffer("just some random stuff", CharsetUtil.UTF_8);
        handler.channelRead(ctx, request);

        verify(channelPipeline, times(1)).remove(handler);
        verify(ctx, times(0)).writeAndFlush(any(ByteBuf.class));
        verify(channelFuture, times(0)).addListener(ChannelFutureListener.CLOSE);
        verify(ctx, times(1)).fireChannelRead(any(ByteBuf.class));
    }

    @Test
    public void testNonByteBufMessageIsIgnored() throws Exception {
        handler.channelRead(ctx, null);

        verifyNoInteractions(channelPipeline);
        verify(ctx, times(0)).writeAndFlush(any(ByteBuf.class));
        verify(channelFuture, times(0)).addListener(ChannelFutureListener.CLOSE);
        verify(ctx, times(1)).fireChannelRead(null);
    }
}