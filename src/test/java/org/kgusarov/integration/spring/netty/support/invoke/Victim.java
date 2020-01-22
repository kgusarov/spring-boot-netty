package org.kgusarov.integration.spring.netty.support.invoke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Victim {
    private static final Logger LOGGER = LoggerFactory.getLogger(Victim.class);

    private String concat(final int a, final int b) {
        LOGGER.info("concat(a,b) invoked");
        return a + "-" + b;
    }

    String concat(final int a, final int b, final String c) {
        LOGGER.info("concat(a,b,c) invoked");
        return a + "-" + b + '-' + c;
    }

    void noArg() {
        LOGGER.info("noArg() invoked");
    }

    public int proxied() {
        LOGGER.info("proxied() invoked");
        return 1;
    }
}
