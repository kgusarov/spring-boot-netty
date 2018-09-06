package org.kgusarov.integration.spring.netty.configuration;

import org.junit.Test;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;
import org.kgusarov.integration.spring.netty.annotations.NettyOnDisconnect;

import java.lang.reflect.Method;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class OnDisconnectMethodComparatorTest {
    private static final Method FIRST_METHOD;
    private static final Method SECOND_METHOD;
    private static final Method THIRD_METHOD;
    private static final Method NON_ANNOTATED_METHOD;
    private static final Method WRONG_ANNOTATION_METHOD;

    static {
        try {
            FIRST_METHOD = OnDisconnectMethodComparatorTest.class.getDeclaredMethod("firstMethod");
            SECOND_METHOD = OnDisconnectMethodComparatorTest.class.getDeclaredMethod("secondMethod");
            THIRD_METHOD = OnDisconnectMethodComparatorTest.class.getDeclaredMethod("thirdMethod");
            NON_ANNOTATED_METHOD = OnDisconnectMethodComparatorTest.class.getDeclaredMethod("nonAnnotatedMethod");
            WRONG_ANNOTATION_METHOD = OnDisconnectMethodComparatorTest.class.getDeclaredMethod("wrongAnnotationMethod");
        } catch (final NoSuchMethodException ignored) {
            throw new IllegalStateException();
        }
    }

    @Test
    public void methodSorting() {
        final SortedSet<Method> methods = new TreeSet<>(NettyControllerConfiguration.ON_DISCONNECT_METHOD_COMPARATOR);
        methods.add(THIRD_METHOD);
        methods.add(SECOND_METHOD);
        methods.add(FIRST_METHOD);

        assertThat(methods, contains(FIRST_METHOD, SECOND_METHOD, THIRD_METHOD));
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void nonAnnotatedMethodSorting() {
        final SortedSet<Method> methods = new TreeSet<>(NettyControllerConfiguration.ON_DISCONNECT_METHOD_COMPARATOR);
        methods.add(NON_ANNOTATED_METHOD);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void wrongAnnotationMethodSorting() {
        final SortedSet<Method> methods = new TreeSet<>(NettyControllerConfiguration.ON_DISCONNECT_METHOD_COMPARATOR);
        methods.add(WRONG_ANNOTATION_METHOD);
    }

    @NettyOnDisconnect(serverName = "whatever", priority = 1)
    private void firstMethod() {
    }

    @NettyOnDisconnect(serverName = "whatever", priority = 2)
    private void secondMethod() {
    }

    @NettyOnDisconnect(serverName = "whatever", priority = 3)
    private void thirdMethod() {
    }

    private void nonAnnotatedMethod() {
    }

    @NettyOnConnect(serverName = "whatever")
    private void wrongAnnotationMethod() {
    }
}