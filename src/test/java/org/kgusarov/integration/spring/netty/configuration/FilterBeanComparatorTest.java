package org.kgusarov.integration.spring.netty.configuration;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import org.junit.Test;
import org.kgusarov.integration.spring.netty.annotations.NettyFilter;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class FilterBeanComparatorTest {
    private static final Object FIRST_BEAN = new FirstHandler();
    private static final Object SECOND_BEAN = new SecondHandler();
    private static final Object THIRD_BEAN = new ThirdHandler();
    private static final Object NON_ANNOTATED_BEAN = new NonAnnotatedHandler();
    private static final Object WRONG_ANNOTATION_BEAN = new WrongAnnotationHandler();

    @Test
    public void methodSorting() {
        final SortedSet<Object> handlers = new TreeSet<>(SpringNettyConfiguration.FILTER_BEAN_COMPARATOR);
        handlers.add(THIRD_BEAN);
        handlers.add(SECOND_BEAN);
        handlers.add(FIRST_BEAN);

        assertThat(handlers, contains(FIRST_BEAN, SECOND_BEAN, THIRD_BEAN));
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void nonAnnotatedMethodSorting() {
        final SortedSet<Object> methods = new TreeSet<>(SpringNettyConfiguration.FILTER_BEAN_COMPARATOR);
        methods.add(NON_ANNOTATED_BEAN);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void wrongAnnotationMethodSorting() {
        final SortedSet<Object> methods = new TreeSet<>(SpringNettyConfiguration.FILTER_BEAN_COMPARATOR);
        methods.add(WRONG_ANNOTATION_BEAN);
    }

    @NettyFilter(serverName = "whatever", priority = 1)
    private static final class FirstHandler extends ChannelDuplexHandler {
        // Nothing here
    }

    @NettyFilter(serverName = "whatever", priority = 2)
    private static final class SecondHandler extends ChannelInboundHandlerAdapter {
        // Nothing here
    }

    @NettyFilter(serverName = "whatever", priority = 3)
    private static final class ThirdHandler extends ChannelOutboundHandlerAdapter {
        // Nothing here
    }

    private static final class NonAnnotatedHandler extends ChannelDuplexHandler {
        // Nothing here
    }

    @ChannelHandler.Sharable
    private static final class WrongAnnotationHandler extends ChannelDuplexHandler {
        // Nothing here
    }
}