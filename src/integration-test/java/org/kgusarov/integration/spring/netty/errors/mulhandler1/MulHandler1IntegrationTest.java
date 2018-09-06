package org.kgusarov.integration.spring.netty.errors.mulhandler1;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class MulHandler1IntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        MulHandler1Application.main("--spring.profiles.active=errors");
    }
}
