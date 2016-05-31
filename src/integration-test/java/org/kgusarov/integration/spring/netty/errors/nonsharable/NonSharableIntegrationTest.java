package org.kgusarov.integration.spring.netty.errors.nonsharable;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class NonSharableIntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() throws Exception {
        NonSharableApplication.main("--spring.profiles.active=errors");
    }
}
