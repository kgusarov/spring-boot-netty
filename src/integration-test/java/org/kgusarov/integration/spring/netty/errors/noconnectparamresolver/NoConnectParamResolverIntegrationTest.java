package org.kgusarov.integration.spring.netty.errors.noconnectparamresolver;

import org.junit.Test;
import org.kgusarov.integration.spring.netty.errors.duplicatedefs.DuplicateDefsApplication;
import org.springframework.beans.factory.BeanCreationException;

public class NoConnectParamResolverIntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        NoConnectParamResolverApplication.main("--spring.profiles.active=errors");
    }
}
