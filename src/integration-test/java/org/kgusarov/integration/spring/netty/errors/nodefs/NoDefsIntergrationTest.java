package org.kgusarov.integration.spring.netty.errors.nodefs;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class NoDefsIntergrationTest {
    @Test(expected = BeanCreationException.class)
    public void testThereShouldBeAnException() {
        NoDefsApplication.main("--spring.profiles.active=nodefs");
    }
}
