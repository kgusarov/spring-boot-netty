package org.kgusarov.integration.spring.netty.errors.mulhandler3;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class MulHandler3IntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        MulHandler3Application.main("--spring.profiles.active=errors");
    }
}
