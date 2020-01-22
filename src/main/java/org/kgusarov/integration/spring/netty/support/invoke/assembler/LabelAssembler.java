package org.kgusarov.integration.spring.netty.support.invoke.assembler;

import org.objectweb.asm.Label;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Internal API: code generation support - labels
 */
final class LabelAssembler {
    private static final AtomicInteger LABEL_COUNTER = new AtomicInteger(0);

    private LabelAssembler() {
    }

    static Label createLabel() {
        final Label result = new Label();
        result.info = LABEL_COUNTER.getAndIncrement();
        return result;
    }
}
