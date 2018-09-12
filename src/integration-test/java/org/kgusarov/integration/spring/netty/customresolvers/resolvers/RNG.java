package org.kgusarov.integration.spring.netty.customresolvers.resolvers;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RNG extends Random {
    private final List<Long> generatedNumbers = Lists.newArrayList();

    @Override
    public long nextLong() {
        final long result = super.nextLong();
        generatedNumbers.add(result);
        return result;
    }

    public List<Long> getGeneratedNumbers() {
        return generatedNumbers;
    }
}
