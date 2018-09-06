package org.kgusarov.integration.spring.netty.onconnect;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.etc.ClientHandler;
import org.kgusarov.integration.spring.netty.etc.CyclicWaitForProcessingToComplete;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCallStack;
import org.kgusarov.integration.spring.netty.onconnect.handlers.OnConnectController;
import org.kgusarov.integration.spring.netty.onconnect.handlers.TransactionalOnConnectController;
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

@ActiveProfiles("onconnect")
@SpringBootTest
@ContextConfiguration(classes = {
        OnConnectApplication.class,
        HandlerMethodCallStack.class
}, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class OnConnectIntegrationTest {
    @Autowired
    private NettyServers servers;

    @Autowired
    private HandlerMethodCallStack handlerCallStack;

    @Autowired
    private CyclicWaitForProcessingToComplete counter;

    @Test
    @DirtiesContext
    public void testServersShouldBePresent() {
        assertThat(servers, not(hasSize(0)));
    }

    @Test
    @DirtiesContext
    @SuppressWarnings("unchecked")
    public void testHandlersAddedInCorrectOrder() throws Exception {
        final SettableFuture<Long> r1 = SettableFuture.create();
        final SettableFuture<Long> r2 = SettableFuture.create();
        final SettableFuture<Long> r3 = SettableFuture.create();
        final SettableFuture<Long> r4 = SettableFuture.create();
        final SettableFuture<Long> r5 = SettableFuture.create();
        final SettableFuture<Long> r6 = SettableFuture.create();

        final ClientHandler ch = new ClientHandler(r1, r2, r3, r4, r5, r6);
        final ServerClient client = new ServerClient(40000, "localhost", ch);

        doConnectionCycle(r1, r2, r3, client, 0);
        doConnectionCycle(r4, r5, r6, client, 1);

        assertThat(handlerCallStack, contains(
                is(OnConnectController.ON_CONNECT1),
                is(OnConnectController.ON_CONNECT2),
                is(TransactionalOnConnectController.ON_CONNECT),
                is(OnConnectController.ON_CONNECT1),
                is(OnConnectController.ON_CONNECT2),
                is(TransactionalOnConnectController.ON_CONNECT)
        ));
    }

    private void doConnectionCycle(final SettableFuture<Long> f1, final SettableFuture<Long> f2,
                                   final SettableFuture<Long> f3, final ServerClient client, final int phase)
            throws InterruptedException, ExecutionException, TimeoutException {

        client.connect().get();
        counter.awaitAdvanceInterruptibly(phase, 30, TimeUnit.SECONDS);
        Futures.successfulAsList(f1, f2, f3).get(30, TimeUnit.SECONDS);

        assertEquals(92L, (long) f1.get());
        assertEquals(87L, (long) f2.get());
        assertEquals(106L, (long) f3.get());

        client.disconnect();
    }
}
