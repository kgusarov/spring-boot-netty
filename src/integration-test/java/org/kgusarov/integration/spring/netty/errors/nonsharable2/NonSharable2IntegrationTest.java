package org.kgusarov.integration.spring.netty.errors.nonsharable2;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class NonSharable2IntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        NonSharable2Application.main("--spring.profiles.active=errors");
    }
}
