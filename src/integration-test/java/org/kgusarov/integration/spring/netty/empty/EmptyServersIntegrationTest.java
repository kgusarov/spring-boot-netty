package org.kgusarov.integration.spring.netty.empty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgusarov.integration.spring.netty.configuration.NettyServers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@SpringBootTest
@ContextConfiguration(classes = EmptyApplication.class, loader = SpringBootContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EmptyServersIntegrationTest {
    @Autowired
    private NettyServers servers;

    @Test
    public void testNoServerDefinitionsWillResultInEmptyServerList() {
        assertThat(servers, hasSize(0));
    }
}
