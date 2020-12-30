package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class Source_FactoryTest {

    @BeforeAll
    static void beforeAll() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Test
    @Execution(CONCURRENT)
    void testMakeSourceChain() {
        // TODO test that a single one create a single End Source
        // TODO test that multiple ones create a step chain with a EndSource at the end
        // TODO test that the individual calculator is attached at the correct layer
    }

    @Test
    @Execution(CONCURRENT)
    void makeSharedStepSourceChain() {
        // TODO test that all created source share the same entry point
        // TODO check that they are all 2 layer with an end at the end
    }
}
