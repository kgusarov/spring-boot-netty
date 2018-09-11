package org.kgusarov.integration.spring.netty.errors.nonsharable1;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class NonSharable1IntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        NonSharable1Application.main("--spring.profiles.active=errors");
    }
}
