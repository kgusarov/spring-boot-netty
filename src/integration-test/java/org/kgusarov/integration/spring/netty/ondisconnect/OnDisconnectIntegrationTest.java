package org.kgusarov.integration.spring.netty.ondisconnect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
import org.kgusarov.integration.spring.netty.ondisconnect.handlers.OnDisconnectController;
import org.kgusarov.integration.spring.netty.ondisconnect.handlers.TransactionalOnDisconnectController;
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
import static org.junit.Assert.assertThat;

@ActiveProfiles("ondisconnect")
@SpringBootTest
@ContextConfiguration(classes = {
        OnDisconnectApplication.class,
        HandlerMethodCalls.class
}, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class OnDisconnectIntegrationTest {
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
    @SuppressWarnings("unchecked")
    public void testHandlersAddedInCorrectOrder() throws Exception {
        final ServerClient client = new ServerClient(40000, "localhost");

        doDisconnectCycle(client, 0);
        doDisconnectCycle(client, 1);

        assertThat(calls, contains(
                is(OnDisconnectController.ON_DISCONNECT1),
                is(OnDisconnectController.ON_DISCONNECT2),
                is(TransactionalOnDisconnectController.ON_DISCONNECT),
                is(OnDisconnectController.ON_DISCONNECT1),
                is(OnDisconnectController.ON_DISCONNECT2),
                is(TransactionalOnDisconnectController.ON_DISCONNECT)
        ));
    }

    private void doDisconnectCycle(final ServerClient client, final int phase) throws InterruptedException, ExecutionException, TimeoutException {
        client.connect().get().disconnect();
        counter.awaitAdvanceInterruptibly(phase, 30, TimeUnit.SECONDS);
    }
}
