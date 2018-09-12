package org.kgusarov.integration.spring.netty.etc;

import org.springframework.stereotype.Component;

import java.util.concurrent.Phaser;

@Component
public class ProcessingCounter extends Phaser {
    public ProcessingCounter(final int count) {
        super(count);
    }
}
