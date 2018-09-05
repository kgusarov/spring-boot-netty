package org.kgusarov.integration.spring.netty.etc;

import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class WaitForProcessingToComplete extends CountDownLatch {
    public WaitForProcessingToComplete(final int count) {
        super(count);
    }
}
