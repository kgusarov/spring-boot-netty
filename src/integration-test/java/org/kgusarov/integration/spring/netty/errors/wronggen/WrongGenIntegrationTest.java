package org.kgusarov.integration.spring.netty.errors.wronggen;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class WrongGenIntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        WrongGenApplication.main("--spring.profiles.active=errors");
    }
}
