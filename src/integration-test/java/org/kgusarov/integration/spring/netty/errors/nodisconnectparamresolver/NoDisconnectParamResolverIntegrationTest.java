package org.kgusarov.integration.spring.netty.errors.nodisconnectparamresolver;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class NoDisconnectParamResolverIntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        NoDisconnectParamResolverApplication.main("--spring.profiles.active=errors");
    }
}
