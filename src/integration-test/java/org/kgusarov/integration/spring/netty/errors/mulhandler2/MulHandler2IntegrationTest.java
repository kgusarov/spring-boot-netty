package org.kgusarov.integration.spring.netty.errors.mulhandler2;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class MulHandler2IntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        MulHandler2Application.main("--spring.profiles.active=errors");
    }
}
