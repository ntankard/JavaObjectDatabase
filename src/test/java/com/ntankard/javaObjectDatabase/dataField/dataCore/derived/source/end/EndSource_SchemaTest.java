package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.end;

import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.DatabaseFactory;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.End_N_TestObject;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step1_N_TestObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.util.Arrays;
import java.util.List;

import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.End_N_TestObject.NData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step1_N_TestObject.NEndLink;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step1_N_TestObject.NEndNData;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class EndSource_SchemaTest {

    @BeforeAll
    static void beforeAll() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Test
    @Execution(CONCURRENT)
    void constructor() {
        assertThrows(AssertionError.class, () -> new End_Source_Schema<>(null));
        assertDoesNotThrow(() -> new End_Source_Schema<>("TestKey"));
        assertDoesNotThrow(() -> new End_Source_Schema<>("TestKey", null));
        assertDoesNotThrow(() -> new End_Source_Schema<>("TestKey", (parent, oldValue, newValue) -> {
        }));
    }

    @Test
    @Execution(CONCURRENT)
    void createRootSource() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(End_N_TestObject.class, Step1_N_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);

        Step1_N_TestObject dummyStep = new Step1_N_TestObject(null, database).add();
        Derived_DataCore<?, ?> dummyCore = (Derived_DataCore<?, ?>) dummyStep.getField(NEndNData).getDataCore();

        End_Source_Schema<?> valid = new End_Source_Schema<Integer>(NEndLink);
        assertDoesNotThrow(() -> valid.createRootSource(dummyCore));

        End_Source_Schema<?> invalid = new End_Source_Schema<Integer>("TestKey");
        assertThrows(AssertionError.class, () -> invalid.createRootSource(dummyCore));
        assertThrows(AssertionError.class, () -> invalid.createRootSource(null));
    }

    @SuppressWarnings({"ConstantConditions", "rawtypes"})
    @Test
    @Execution(CONCURRENT)
    void createChildSource() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(End_N_TestObject.class, Step1_N_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);

        Step1_N_TestObject dummyStep = new Step1_N_TestObject(null, database).add();
        Source dummySource = ((Derived_DataCore<?, ?>) dummyStep.getField(NEndNData).getDataCore()).getSources()[0];
        End_N_TestObject dummyObject = new End_N_TestObject(5, database);

        End_Source_Schema<?> valid = new End_Source_Schema<Integer>(NData);
        assertDoesNotThrow(() -> valid.createChildSource(dummyObject, dummySource));

        assertThrows(AssertionError.class, () -> valid.createChildSource(dummyObject, null));
        assertThrows(AssertionError.class, () -> valid.createChildSource(null, dummySource));

        End_Source_Schema<?> invalid = new End_Source_Schema<Integer>("TestKey");
        assertThrows(AssertionError.class, () -> invalid.createChildSource(dummyObject, dummySource));

        End_Source_Schema<?> invalid2 = new End_Source_Schema<Integer>(NData, (parent, oldValue, newValue) -> {
        });
        assertThrows(AssertionError.class, () -> invalid2.createChildSource(dummyObject, dummySource));
    }
}
