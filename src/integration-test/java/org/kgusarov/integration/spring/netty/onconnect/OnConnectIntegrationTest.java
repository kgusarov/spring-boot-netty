package org.kgusarov.integration.spring.netty.onconnect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCallStack;
import org.kgusarov.integration.spring.netty.etc.WaitForProcessingToComplete;
import org.kgusarov.integration.spring.netty.onconnect.handlers.OnConnectController;
import org.kgusarov.integration.spring.netty.onconnect.handlers.TransactionalOnConnectController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
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
    private WaitForProcessingToComplete waitForProcessingToComplete;

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

        client.connect().get().disconnect();
        client.connect().get().disconnect();

        waitForProcessingToComplete.await(30, TimeUnit.SECONDS);

        assertThat(handlerCallStack, contains(
                is(OnConnectController.ON_CONNECT1),
                is(OnConnectController.ON_CONNECT2),
                is(TransactionalOnConnectController.ON_CONNECT),
                is(OnConnectController.ON_CONNECT1),
                is(OnConnectController.ON_CONNECT2),
                is(TransactionalOnConnectController.ON_CONNECT)
        ));
    }
}
