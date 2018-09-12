package org.kgusarov.integration.spring.netty.errors.nomessageparamresolver;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class NoMessageParamResolverIntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        NoMessageParamResolverApplication.main("--spring.profiles.active=errors");
    }
}
