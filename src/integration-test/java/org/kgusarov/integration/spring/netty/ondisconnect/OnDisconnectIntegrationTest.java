package org.kgusarov.integration.spring.netty.ondisconnect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.etc.HandlerCallStack;
import org.kgusarov.integration.spring.netty.etc.HandlerStack;
import org.kgusarov.integration.spring.netty.etc.TcpEventStack;
import org.kgusarov.integration.spring.netty.etc.WaitForProcessingToComplete;
import org.kgusarov.integration.spring.netty.ondisconnect.handlers.OnDisconnectHandler1;
import org.kgusarov.integration.spring.netty.ondisconnect.handlers.OnDisconnectHandler2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@ActiveProfiles("ondisconnect")
@IntegrationTest
@ContextConfiguration(classes = {
        OnDisconnectApplication.class,
        HandlerCallStack.class,
        TcpEventStack.class,
        HandlerStack.class
}, loader = SpringApplicationContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class OnDisconnectIntegrationTest {
    @Autowired
    private NettyServers servers;

    @Autowired
    private HandlerCallStack handlerCallStack;

    @Autowired
    private TcpEventStack tcpEventStack;

    @Autowired
    private WaitForProcessingToComplete waitForProcessingToComplete;

    @Autowired
    private HandlerStack handlers;

    @Test
    @DirtiesContext
    public void testServersShouldBePresent() throws Exception {
        assertThat(servers, not(hasSize(0)));
    }

    @Test
    @DirtiesContext
    public void testHandlersAddedInCorrectOrder() throws Exception {
        final ServerClient client = new ServerClient(40000, "localhost");

        client.connect().get().disconnect();
        client.connect().get().disconnect();

        waitForProcessingToComplete.await(30, TimeUnit.SECONDS);

        assertEquals(4, handlerCallStack.size());
        assertEquals(4, tcpEventStack.size());

        assertThat(handlerCallStack.get(0), is(equalTo(OnDisconnectHandler1.class)));
        assertThat(handlerCallStack.get(1), is(equalTo(OnDisconnectHandler2.class)));
        assertThat(handlerCallStack.get(2), is(equalTo(OnDisconnectHandler1.class)));
        assertThat(handlerCallStack.get(3), is(equalTo(OnDisconnectHandler2.class)));

        assertEquals(tcpEventStack.get(0), tcpEventStack.get(1));
        assertEquals(tcpEventStack.get(2), tcpEventStack.get(3));

        assertNotEquals(handlers.get(0), handlers.get(2));
        assertNotEquals(handlers.get(1), handlers.get(3));
    }
}
