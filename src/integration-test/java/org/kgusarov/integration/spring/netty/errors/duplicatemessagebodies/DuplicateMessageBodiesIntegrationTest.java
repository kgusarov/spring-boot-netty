package org.kgusarov.integration.spring.netty.errors.duplicatemessagebodies;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class DuplicateMessageBodiesIntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        DuplicateMessageBodiesApplication.main("--spring.profiles.active=errors");
    }
}
