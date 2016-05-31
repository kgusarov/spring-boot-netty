package org.kgusarov.integration.spring.netty.errors.nonhandler2;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class NonHandler2IntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() throws Exception {
        NonHandler2Application.main("--spring.profiles.active=errors");
    }
}
