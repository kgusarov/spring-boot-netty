package org.kgusarov.integration.spring.netty.errors.nonhandler1;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class NonHandler1IntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        NonHandler1Application.main("--spring.profiles.active=errors");
    }
}
