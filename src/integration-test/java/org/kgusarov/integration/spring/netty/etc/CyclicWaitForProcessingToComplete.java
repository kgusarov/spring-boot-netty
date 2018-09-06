package org.kgusarov.integration.spring.netty.etc;

import org.springframework.stereotype.Component;

import java.util.concurrent.Phaser;

@Component
public class CyclicWaitForProcessingToComplete extends Phaser {
    public CyclicWaitForProcessingToComplete(final int count) {
        super(count);
    }
}
