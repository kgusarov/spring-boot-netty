package org.kgusarov.integration.spring.netty.support.invoke.assembler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundInvoker;
import org.kgusarov.integration.spring.netty.support.invoke.MethodHandleCreator;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnConnectParameterResolver;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnDisconnectParameterResolver;
import org.kgusarov.integration.spring.netty.support.resolvers.NettyOnMessageParameterResolver;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * Internal API: code generation support - descriptors and other string constants
 */
@SuppressWarnings("WeakerAccess")
public final class Descriptors {
    public static final String MHC_INTERNAL_NAME = Type.getType(MethodHandleCreator.class).getInternalName();
    public static final String CHANNEL_INTERNAL_NAME = Type.getType(Channel.class).getInternalName();

    public static final String MH_DESCRIPTOR = Type.getDescriptor(MethodHandle.class);
    public static final String MH_INTERNAL_NAME = Type.getType(MethodHandle.class).getInternalName();

    public static final String CL_DESCRIPTOR = Type.getDescriptor(Class.class);
    public static final String CLA_DESCRIPTOR = Type.getDescriptor(Class[].class);
    public static final String STR_DESCRIPTOR = Type.getDescriptor(String.class);
    public static final String OBJ_DESCRIPTOR = Type.getDescriptor(Object.class);

    public static final String ONC_RESA_DESCRIPTOR = Type.getDescriptor(NettyOnConnectParameterResolver[].class);
    public static final String ONC_RES_INTERNAL_NAME = Type.getType(NettyOnConnectParameterResolver.class).getInternalName();
    public static final String ONC_RESOLVE_DESCRIPTOR;

    public static final String OND_RESA_DESCRIPTOR = Type.getDescriptor(NettyOnDisconnectParameterResolver[].class);
    public static final String OND_RES_INTERNAL_NAME = Type.getType(NettyOnDisconnectParameterResolver.class).getInternalName();
    public static final String OND_RESOLVE_DESCRIPTOR;

    public static final String ONM_RESA_DESCRIPTOR = Type.getDescriptor(NettyOnMessageParameterResolver[].class);
    public static final String ONM_RES_INTERNAL_NAME = Type.getType(NettyOnMessageParameterResolver.class).getInternalName();
    public static final String ONM_RESOLVE_DESCRIPTOR;

    public static final String CHANNEL_WRITE_AND_FLUSH_DESCRIPTOR;

    static {
        try {
            final Method oncResolve = NettyOnConnectParameterResolver.class
                    .getDeclaredMethod("resolve", ChannelHandlerContext.class);
            ONC_RESOLVE_DESCRIPTOR = Type.getMethodDescriptor(oncResolve);

            final Method ondResolve = NettyOnDisconnectParameterResolver.class
                    .getDeclaredMethod("resolve", ChannelFuture.class);
            OND_RESOLVE_DESCRIPTOR = Type.getMethodDescriptor(ondResolve);

            final Method onmResolve = NettyOnMessageParameterResolver.class
                    .getDeclaredMethod("resolve", ChannelHandlerContext.class, Object.class);
            ONM_RESOLVE_DESCRIPTOR = Type.getMethodDescriptor(onmResolve);

            final Method writeAndFlush = ChannelOutboundInvoker.class
                    .getDeclaredMethod("writeAndFlush", Object.class);
            CHANNEL_WRITE_AND_FLUSH_DESCRIPTOR = Type.getMethodDescriptor(writeAndFlush);
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private Descriptors() {
    }
}
