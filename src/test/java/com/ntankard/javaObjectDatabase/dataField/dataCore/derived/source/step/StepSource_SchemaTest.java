package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.step;

import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.end.EndSource_Schema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class StepSource_SchemaTest {

    @BeforeAll
    static void beforeAll() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Test
    @Execution(CONCURRENT)
    void constructor() {
        EndSource_Schema<?> dummyEnd = new EndSource_Schema<>("TestKey");

        assertThrows(AssertionError.class, () -> new StepSource_Schema<>(null, dummyEnd));
        assertDoesNotThrow(() -> new StepSource_Schema<>("TestKey", dummyEnd));
        assertDoesNotThrow(() -> new StepSource_Schema<>("TestKey", dummyEnd, null));
        assertDoesNotThrow(() -> new StepSource_Schema<>("TestKey", dummyEnd, (parent, oldValue, newValue) -> {
        }));
    }

    @Test
    @Execution(CONCURRENT)
    void createRootSource() {
        // TODO test parameter rejection
        // TODO test the correct type is generated
    }

    @Test
    @Execution(CONCURRENT)
    void createChildSource() {
        // TODO test parameter rejection
        // TODO test the correct type is generated
        // TODO test that when using this method you cant have set a individual calculator
    }
}
