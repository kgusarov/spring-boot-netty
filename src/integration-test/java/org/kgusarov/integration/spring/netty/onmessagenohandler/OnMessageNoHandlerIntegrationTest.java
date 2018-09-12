package org.kgusarov.integration.spring.netty.onmessagenohandler;

import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.ServerClient;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.kgusarov.integration.spring.netty.etc.HandlerMethodCalls;
import org.kgusarov.integration.spring.netty.etc.ProcessingCounter;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("onmessage")
@SpringBootTest
@ContextConfiguration(classes = {
        OnMessageNoHandlerApplication.class,
        HandlerMethodCalls.class}, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class OnMessageNoHandlerIntegrationTest {
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
    public void testNandlersNotCalled() throws Exception {
        runTestOnce(0);
        runTestOnce(1);
    }

    private void runTestOnce(final int phase) throws InterruptedException, TimeoutException, ExecutionException {
        calls.clear();

        final ServerClient client = new ServerClient(40000, "localhost");

        client.connect();
        client.writeAndFlush(Unpooled.copyLong(100500L)).syncUninterruptibly();
        counter.awaitAdvanceInterruptibly(phase, 30, TimeUnit.SECONDS);
        client.disconnect();

        assertTrue(calls.isEmpty());
    }
}
