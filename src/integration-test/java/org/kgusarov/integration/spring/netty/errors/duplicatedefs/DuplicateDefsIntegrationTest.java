package org.kgusarov.integration.spring.netty.errors.duplicatedefs;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class DuplicateDefsIntegrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() throws Exception {
        DuplicateDefsApplication.main("--spring.profiles.active=duplicatedefs");
    }
}
