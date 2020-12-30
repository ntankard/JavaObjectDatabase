package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.step;

import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.SingleChain;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.DatabaseFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.util.Arrays;
import java.util.List;

import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.End_TestObject.Data;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.Step1_TestObject.EndData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.Step1_TestObject.EndLink;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.Step2_TestObject.S1EndData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.Step2_TestObject.S1Link;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.Step3_TestObject.S2Link;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.Step3_TestObject.S2S1EndData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.End_N_TestObject.NData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step1_N_TestObject.NEndLink;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step1_N_TestObject.NEndNData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step2_N_TestObject.NS1Link;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step2_N_TestObject.NS1NEndNData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step3_N_TestObject.NS2Link;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step3_N_TestObject.NS2NS1NEndNData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.SingleChain.SingleEnd_TestObject.CoreData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.SingleChain.SingleEnd_TestObject.NullableCoreData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.SingleChain.Step_TestObject.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class Step_SourceTest {

    // TODO check for full coverage

    @BeforeAll
    static void beforeAll() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    /**
     * Test that a field using a single Step_Source and a End_Source where both can not be null.
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    @Execution(CONCURRENT)
    void singleStepSource_NonNullableStep_NonNullableEnd() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(SingleChain.SingleEnd_TestObject.class, SingleChain.Step_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);
        SingleChain.SingleEnd_TestObject core1;
        SingleChain.SingleEnd_TestObject core2;
        SingleChain.Step_TestObject tO;

        String coreData_key = CoreData;
        String link1_key = Link1;
        String link1_coreData_key = Link1_CoreData;

        // Check the core objects are as expected for the test
        core1 = new SingleChain.SingleEnd_TestObject(333, database);
        core2 = new SingleChain.SingleEnd_TestObject(222, database);
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when constructed
        tO = new SingleChain.Step_TestObject(core1, database);
        assertEquals(333, tO.<Integer>get(link1_coreData_key));
        assertEquals(333, core1.<Integer>get(coreData_key));
        assertEquals(222, core2.<Integer>get(coreData_key));
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the end is updated
        core1.set(coreData_key, 555);
        assertEquals(555, tO.<Integer>get(link1_coreData_key));
        assertEquals(555, core1.<Integer>get(coreData_key));
        assertEquals(222, core2.<Integer>get(coreData_key));
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the step is updated
        tO.set(link1_key, core2);
        assertEquals(222, tO.<Integer>get(link1_coreData_key));
        assertEquals(555, core1.<Integer>get(coreData_key));
        assertEquals(222, core2.<Integer>get(coreData_key));
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the new end is updated
        core2.set(coreData_key, 111);
        assertEquals(111, tO.<Integer>get(link1_coreData_key));
        assertEquals(555, core1.<Integer>get(coreData_key));
        assertEquals(111, core2.<Integer>get(coreData_key));
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the old end is updated
        core1.set(coreData_key, 888);
        assertEquals(111, tO.<Integer>get(link1_coreData_key));
        assertEquals(888, core1.<Integer>get(coreData_key));
        assertEquals(111, core2.<Integer>get(coreData_key));
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());
    }

    /**
     * Test that a field using a single Step_Source and a End_Source where the step can not be null but the end can.
     */
    @SuppressWarnings({"UnnecessaryLocalVariable", "SimplifiableAssertion"})
    @Test
    @Execution(CONCURRENT)
    void singleStepSource_NonNullStep_NullableEnd() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(SingleChain.SingleEnd_TestObject.class, SingleChain.Step_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);
        SingleChain.SingleEnd_TestObject core1;
        SingleChain.SingleEnd_TestObject core2;
        SingleChain.SingleEnd_TestObject tempCore;
        SingleChain.Step_TestObject tO;

        String coreData_key = NullableCoreData;
        String link1_key = Link1;
        String link1_coreData_key = Link1_NullableCoreData;

        // Check the core objects are as expected for the test
        core1 = new SingleChain.SingleEnd_TestObject(333, 33, database);
        core2 = new SingleChain.SingleEnd_TestObject(222, 22, database);
        tempCore = new SingleChain.SingleEnd_TestObject(-1, null, database);
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when constructed
        tO = new SingleChain.Step_TestObject(tempCore, database);
        assertEquals(null, tO.<Integer>get(link1_coreData_key));
        assertEquals(33, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when constructed and the end is null
        tO = new SingleChain.Step_TestObject(core1, database);
        assertEquals(33, tO.<Integer>get(link1_coreData_key));
        assertEquals(33, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the end is updated
        core1.set(coreData_key, 55);
        assertEquals(55, tO.<Integer>get(link1_coreData_key));
        assertEquals(55, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the step is updated
        tO.set(link1_key, core2);
        assertEquals(22, tO.<Integer>get(link1_coreData_key));
        assertEquals(55, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the new end is updated
        core2.set(coreData_key, 11);
        assertEquals(11, tO.<Integer>get(link1_coreData_key));
        assertEquals(55, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the old end is updated
        core1.set(coreData_key, 88);
        assertEquals(11, tO.<Integer>get(link1_coreData_key));
        assertEquals(88, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the end value is null
        core2.set(coreData_key, null);
        assertEquals(null, tO.<Integer>get(link1_coreData_key));
        assertEquals(88, core1.<Integer>get(coreData_key));
        assertEquals(null, core2.<Integer>get(coreData_key));
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the end is set to something after null
        core2.set(coreData_key, 99);
        assertEquals(99, tO.<Integer>get(link1_coreData_key));
        assertEquals(88, core1.<Integer>get(coreData_key));
        assertEquals(99, core2.<Integer>get(coreData_key));
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());
    }

    /**
     * Test that a field using a single Step_Source and a End_Source where the step can not be null but the end can.
     */
    @SuppressWarnings("SimplifiableAssertion")
    @Test
    @Execution(CONCURRENT)
    void singleStepSource_NullableStep_NonNullEnd() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(SingleChain.SingleEnd_TestObject.class, SingleChain.Step_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);
        SingleChain.SingleEnd_TestObject ignoreCore;
        SingleChain.SingleEnd_TestObject core1;
        SingleChain.SingleEnd_TestObject core2;

        SingleChain.Step_TestObject tO;

        String coreData_key = CoreData;
        String link1_key = NullableLink1;
        String link1_coreData_key = NullableLink1_CoreData;

        // Check the core objects are as expected for the test
        ignoreCore = new SingleChain.SingleEnd_TestObject(-1, -1, database);
        core1 = new SingleChain.SingleEnd_TestObject(33, database);
        core2 = new SingleChain.SingleEnd_TestObject(22, database);
        assertEquals(2, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when constructed with null
        tO = new SingleChain.Step_TestObject(ignoreCore, null, database);
        assertEquals(null, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(33, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are when the first step is set
        tO.set(link1_key, core1);
        assertEquals(33, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(33, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the end is updated
        core1.set(coreData_key, 55);
        assertEquals(55, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(55, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the step is updated
        tO.set(link1_key, core2);
        assertEquals(22, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(55, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the new end is updated
        core2.set(coreData_key, 11);
        assertEquals(11, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(55, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the old end is updated
        core1.set(coreData_key, 88);
        assertEquals(11, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(88, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the step is set to null
        tO.set(link1_key, null);
        assertEquals(null, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(88, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the step is set to something else after null
        tO.set(link1_key, core1);
        assertEquals(88, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(88, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());
    }

    /**
     * Test that a field using a single Step_Source and a End_Source where both the step and the end can be null.
     */
    @SuppressWarnings("SimplifiableAssertion")
    @Test
    @Execution(CONCURRENT)
    void singleStepSource_NullableStep_NullableEnd() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(SingleChain.SingleEnd_TestObject.class, SingleChain.Step_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);
        SingleChain.SingleEnd_TestObject ignoreCore;
        SingleChain.SingleEnd_TestObject core1;
        SingleChain.SingleEnd_TestObject core2;

        SingleChain.Step_TestObject tO;

        String coreData_key = NullableCoreData;
        String link1_key = NullableLink1;
        String link1_coreData_key = NullableLink1_NullableCoreData;

        // Check the core objects are as expected for the test
        ignoreCore = new SingleChain.SingleEnd_TestObject(-1, -1, database);
        core1 = new SingleChain.SingleEnd_TestObject(333, 33, database);
        core2 = new SingleChain.SingleEnd_TestObject(222, 22, database);
        assertEquals(2, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when constructed with null
        tO = new SingleChain.Step_TestObject(ignoreCore, null, database);
        assertEquals(null, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(33, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are when the first step is set
        tO.set(link1_key, core1);
        assertEquals(33, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(33, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the end is updated
        core1.set(coreData_key, 55);
        assertEquals(55, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(55, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the step is updated
        tO.set(link1_key, core2);
        assertEquals(22, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(55, core1.<Integer>get(coreData_key));
        assertEquals(22, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the new end is updated
        core2.set(coreData_key, 11);
        assertEquals(11, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(55, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the old end is updated
        core1.set(coreData_key, 88);
        assertEquals(11, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(88, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the step is set to null
        tO.set(link1_key, null);
        assertEquals(null, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(88, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the step is set to something else after null
        tO.set(link1_key, core1);
        assertEquals(88, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(88, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the end value is null
        core1.set(coreData_key, null);
        assertEquals(null, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(null, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when the end is set to something after null
        core1.set(coreData_key, 99);
        assertEquals(99, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(99, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when both are null
        tO.set(link1_key, null);
        core1.set(coreData_key, null);
        assertEquals(null, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(null, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct the step is set to a field that has null
        tO.set(link1_key, core1);
        core1.set(coreData_key, null);
        assertEquals(null, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(null, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());

        // Check that the values are correct when both are set back to not null
        core1.set(coreData_key, 9999);
        assertEquals(9999, tO.<Integer>get(link1_coreData_key));
        assertEquals(-1, ignoreCore.<Integer>get(coreData_key));
        assertEquals(9999, core1.<Integer>get(coreData_key));
        assertEquals(11, core2.<Integer>get(coreData_key));
        assertEquals(3, ignoreCore.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(3, core1.getField(coreData_key).getFieldChangeListeners().size());
        assertEquals(2, core2.getField(coreData_key).getFieldChangeListeners().size());
    }

    /**
     * Test that a field using a multiple steps works as expected when all steps start as null and are initialised bottom to top.
     * <p>
     * Step3   Null:Null       Step3  Null:Null       Step3  Null:Null       Step3  Null:Null       Step3  Step2:222
     * Step2   Null:Null   ->  Step2  Null:Null   ->  Step2  Null:Null   ->  Step2  Step1:222   ->  Step2  Step1:222
     * Step1   Null:Null   ->  Step1  Null:Null   ->  Step1  End:222     ->  Step1  End:222     ->  Step1  End:222
     * End     Null            End    222             End    222             End    222             End    222
     */
    @SuppressWarnings({"SimplifiableAssertion", "PointlessArithmeticExpression"})
    @Test
    @Execution(CONCURRENT)
    void multiStepSource_AllNull_BottomToTop() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(NullableMultiChain.End_N_TestObject.class, NullableMultiChain.Step1_N_TestObject.class, NullableMultiChain.Step2_N_TestObject.class, NullableMultiChain.Step3_N_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);

        NullableMultiChain.End_N_TestObject end;
        NullableMultiChain.Step1_N_TestObject step1;
        NullableMultiChain.Step2_N_TestObject step2;
        NullableMultiChain.Step3_N_TestObject step3;

        // Check the end object is as expected before the test
        end = new NullableMultiChain.End_N_TestObject(null, database).add();
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        // Check the step1 object is as expected before the test
        step1 = new NullableMultiChain.Step1_N_TestObject(null, database).add();
        assertEquals(1 + 1 + 0, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        // Check the step2 object is as expected before the test
        step2 = new NullableMultiChain.Step2_N_TestObject(null, database).add();
        assertEquals(1 + 1 + 0, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(null, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        // Check the step3 object is as expected before the test
        step3 = new NullableMultiChain.Step3_N_TestObject(null, database).add();
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(null, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 0, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(null, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        end.set(NData, 222);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(null, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 0, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(null, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(222, end.<Integer>get(NData));                                                     // 222

        step1.set(NEndLink, end);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(null, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 0, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(null, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));                                                 // end
        assertEquals(222, step1.<Integer>get(NEndNData));                                               // 222
        assertEquals(1 + 1, end.getField(NData).getFieldChangeListeners().size());                      // +1
        assertEquals(222, end.<Integer>get(NData));

        step2.set(NS1Link, step1);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(null, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 0, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));                                              // step2
        assertEquals(222, step2.<Integer>get(NS1NEndNData));                                            // 222
        assertEquals(1 + 1 + 1, step1.getField(NEndLink).getFieldChangeListeners().size());             // +1
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(222, step1.<Integer>get(NEndNData));
        assertEquals(1 + 2, end.getField(NData).getFieldChangeListeners().size());                      // +1
        assertEquals(222, end.<Integer>get(NData));

        step3.set(NS2Link, step2);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));                                              // step2
        assertEquals(222, step3.<Integer>get(NS2NS1NEndNData));                                         // 222
        assertEquals(1 + 1 + 1, step2.getField(NS1Link).getFieldChangeListeners().size());              // +1
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(222, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 2, step1.getField(NEndLink).getFieldChangeListeners().size());             // +1
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(222, step1.<Integer>get(NEndNData));
        assertEquals(1 + 3, end.getField(NData).getFieldChangeListeners().size());                      // +1
        assertEquals(222, end.<Integer>get(NData));
    }

    /**
     * Test that a field using a multiple steps works as expected when all steps start as null and are initialised top to bottom.
     * <p>
     * Step3   Null:Null       Step3  Step2:Null      Step3  Step2:Null     Step3  Step2:Null     Step2  Step2:444
     * Step2   Null:Null   ->  Step2  Null:Null   ->  Step2  Step1:Null ->  Step2  Step1:Null ->  Step2  Step1:444
     * Step1   Null:Null   ->  Step1  Null:Null   ->  Step1  Null:Null  ->  Step1  End:Null   ->  Step1  End:444
     * End     Null            End    Null            End    Null           End    Null           End    444
     */
    @SuppressWarnings({"PointlessArithmeticExpression", "SimplifiableAssertion"})
    @Test
    @Execution(CONCURRENT)
    void multiStepSource_AllNull_TopToBottom() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(NullableMultiChain.End_N_TestObject.class, NullableMultiChain.Step1_N_TestObject.class, NullableMultiChain.Step2_N_TestObject.class, NullableMultiChain.Step3_N_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);

        NullableMultiChain.End_N_TestObject end;
        NullableMultiChain.Step1_N_TestObject step1;
        NullableMultiChain.Step2_N_TestObject step2;
        NullableMultiChain.Step3_N_TestObject step3;


        // Check the end object is as expected before the test
        end = new NullableMultiChain.End_N_TestObject(null, database).add();
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        // Check the step1 object is as expected before the test
        step1 = new NullableMultiChain.Step1_N_TestObject(null, database).add();
        assertEquals(1 + 1 + 0, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        // Check the step2 object is as expected before the test
        step2 = new NullableMultiChain.Step2_N_TestObject(null, database).add();
        assertEquals(1 + 1 + 0, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(null, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        // Check the step3 object is as expected before the test
        step3 = new NullableMultiChain.Step3_N_TestObject(null, database).add();
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(null, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 0, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(null, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        step3.set(NS2Link, step2);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));                                              // step2
        assertEquals(null, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 1, step2.getField(NS1Link).getFieldChangeListeners().size());              //+1
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(null, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        step2.set(NS1Link, step1);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(null, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 1, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));                                              // step1
        assertEquals(null, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 2, step1.getField(NEndLink).getFieldChangeListeners().size());             // +2
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(null, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(null, end.<Integer>get(NData));

        step1.set(NEndLink, end);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(null, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 1, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(null, step2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 2, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));                                                 // end
        assertEquals(null, step1.<Integer>get(NEndNData));
        assertEquals(1 + 3, end.getField(NData).getFieldChangeListeners().size());                      // +3
        assertEquals(null, end.<Integer>get(NData));

        end.set(NData, 444);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(444, step3.<Integer>get(NS2NS1NEndNData));                                         // 444
        assertEquals(1 + 1 + 1, step2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1, step2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(444, step2.<Integer>get(NS1NEndNData));                                            // 444
        assertEquals(1 + 1 + 2, step1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end, step1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(444, step1.<Integer>get(NEndNData));                                               // 444
        assertEquals(1 + 3, end.getField(NData).getFieldChangeListeners().size());
        assertEquals(444, end.<Integer>get(NData));                                                     // 444
    }

    @SuppressWarnings({"PointlessArithmeticExpression"})
    @Test
    @Execution(CONCURRENT)
    void multiStepSource_Nullable_BottomToTop() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(NullableMultiChain.End_N_TestObject.class, NullableMultiChain.Step1_N_TestObject.class, NullableMultiChain.Step2_N_TestObject.class, NullableMultiChain.Step3_N_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);

        NullableMultiChain.End_N_TestObject end_1;
        NullableMultiChain.End_N_TestObject end_2;
        NullableMultiChain.End_N_TestObject end_3;
        NullableMultiChain.End_N_TestObject end_4;
        NullableMultiChain.Step1_N_TestObject step1_1;
        NullableMultiChain.Step1_N_TestObject step1_2;
        NullableMultiChain.Step1_N_TestObject step1_3;
        NullableMultiChain.Step2_N_TestObject step2_1;
        NullableMultiChain.Step2_N_TestObject step2_2;
        NullableMultiChain.Step3_N_TestObject step3;

        // Check the end object is as expected before the test
        end_1 = new NullableMultiChain.End_N_TestObject(1111, database).add();
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        end_2 = new NullableMultiChain.End_N_TestObject(2222, database).add();
        assertEquals(1 + 0, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        end_3 = new NullableMultiChain.End_N_TestObject(3333, database).add();
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 0, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        end_4 = new NullableMultiChain.End_N_TestObject(4444, database).add();
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 0, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        // Check the step1 object is as expected before the test
        step1_1 = new NullableMultiChain.Step1_N_TestObject(end_1, database).add();
        assertEquals(1 + 1 + 0, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 0, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 1, end_1.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(NData));

        step1_2 = new NullableMultiChain.Step1_N_TestObject(end_2, database).add();
        assertEquals(1 + 1 + 0, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 1, end_2.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 1, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        step1_3 = new NullableMultiChain.Step1_N_TestObject(end_3, database).add();
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 1, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 1, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        // Check the step2 object is as expected before the test
        step2_1 = new NullableMultiChain.Step2_N_TestObject(step1_1, database).add();
        assertEquals(1 + 1 + 0, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_1.getField(NEndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 1, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 2, end_1.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(NData));

        step2_2 = new NullableMultiChain.Step2_N_TestObject(step1_2, database).add();
        assertEquals(1 + 1 + 0, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(2222, step2_2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_2.getField(NEndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 2, end_2.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 2, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        // Check the step3 object is as expected before the test
        step3 = new NullableMultiChain.Step3_N_TestObject(step2_1, database).add();
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(1111, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 0, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(2222, step2_2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 1, step2_1.getField(NS1Link).getFieldChangeListeners().size());        // +1
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 2, step1_1.getField(NEndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 2, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 3, end_1.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(NData));

        // Test changing values from bottom to top
        end_1.set(NData, 111);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(111, step3.<Integer>get(NS2NS1NEndNData));                                     // 111
        assertEquals(1 + 1 + 0, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(2222, step2_2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 1, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(111, step2_1.<Integer>get(NS1NEndNData));                                      // 111
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 2, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(111, step1_1.<Integer>get(NEndNData));                                         // 111
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 2, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 3, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(111, end_1.<Integer>get(NData));                                               // 111

        step1_1.set(NEndLink, end_4);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(4444, step3.<Integer>get(NS2NS1NEndNData));                                    // 4444
        assertEquals(1 + 1 + 0, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(2222, step2_2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 1, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(4444, step2_1.<Integer>get(NS1NEndNData));                                     // 4444
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 2, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));                                         // end_4
        assertEquals(4444, step1_1.<Integer>get(NEndNData));                                        // 4444
        assertEquals(1 + 3, end_4.getField(NData).getFieldChangeListeners().size());                // +3
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 2, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());                // -3
        assertEquals(111, end_1.<Integer>get(NData));

        step2_1.set(NS1Link, step1_3);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(3333, step3.<Integer>get(NS2NS1NEndNData));                                    // 3333
        assertEquals(1 + 1 + 0, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(2222, step2_2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 1, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));                                      // step1_3
        assertEquals(3333, step2_1.<Integer>get(NS1NEndNData));                                     // 3333
        assertEquals(1 + 1 + 2, step1_3.getField(NEndLink).getFieldChangeListeners().size());       // +2
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_1.getField(NEndLink).getFieldChangeListeners().size());       // -2
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(4444, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 1, end_4.getField(NData).getFieldChangeListeners().size());                // -2
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 3, end_3.getField(NData).getFieldChangeListeners().size());                // +2
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 2, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(111, end_1.<Integer>get(NData));

        step3.set(NS2Link, step2_2);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));                                        // step2_2
        assertEquals(2222, step3.<Integer>get(NS2NS1NEndNData));                                    // 222
        assertEquals(1 + 1 + 1, step2_2.getField(NS1Link).getFieldChangeListeners().size());        // +1
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(2222, step2_2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step2_1.getField(NS1Link).getFieldChangeListeners().size());        // -1
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(3333, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 1, step1_3.getField(NEndLink).getFieldChangeListeners().size());       // -1
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 2, step1_2.getField(NEndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(4444, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 1, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 2, end_3.getField(NData).getFieldChangeListeners().size());                // -1
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 3, end_2.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(111, end_1.<Integer>get(NData));
    }

    @SuppressWarnings({"PointlessArithmeticExpression"})
    @Test
    @Execution(CONCURRENT)
    void multiStepSource_Nullable_TopToBottom() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(NullableMultiChain.End_N_TestObject.class, NullableMultiChain.Step1_N_TestObject.class, NullableMultiChain.Step2_N_TestObject.class, NullableMultiChain.Step3_N_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);

        NullableMultiChain.End_N_TestObject end_1;
        NullableMultiChain.End_N_TestObject end_2;
        NullableMultiChain.End_N_TestObject end_3;
        NullableMultiChain.End_N_TestObject end_4;
        NullableMultiChain.Step1_N_TestObject step1_1;
        NullableMultiChain.Step1_N_TestObject step1_2;
        NullableMultiChain.Step1_N_TestObject step1_3;
        NullableMultiChain.Step2_N_TestObject step2_1;
        NullableMultiChain.Step2_N_TestObject step2_2;
        NullableMultiChain.Step3_N_TestObject step3;

        // Check the end object is as expected before the test
        end_1 = new NullableMultiChain.End_N_TestObject(1111, database).add();
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        end_2 = new NullableMultiChain.End_N_TestObject(2222, database).add();
        assertEquals(1 + 0, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        end_3 = new NullableMultiChain.End_N_TestObject(3333, database).add();
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 0, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        end_4 = new NullableMultiChain.End_N_TestObject(4444, database).add();
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 0, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 0, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        // Check the step1 object is as expected before the test
        step1_1 = new NullableMultiChain.Step1_N_TestObject(end_1, database).add();
        assertEquals(1 + 1 + 0, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 0, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 1, end_1.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(NData));

        step1_2 = new NullableMultiChain.Step1_N_TestObject(end_2, database).add();
        assertEquals(1 + 1 + 0, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 1, end_2.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 1, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        step1_3 = new NullableMultiChain.Step1_N_TestObject(end_3, database).add();
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 1, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 1, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        // Check the step2 object is as expected before the test
        step2_1 = new NullableMultiChain.Step2_N_TestObject(step1_1, database).add();
        assertEquals(1 + 1 + 0, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_1.getField(NEndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 1, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 2, end_1.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(NData));

        step2_2 = new NullableMultiChain.Step2_N_TestObject(step1_2, database).add();
        assertEquals(1 + 1 + 0, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(2222, step2_2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_2.getField(NEndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 2, end_2.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 2, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        // Check the step3 object is as expected before the test
        step3 = new NullableMultiChain.Step3_N_TestObject(step2_1, database).add();
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(1111, step3.<Integer>get(NS2NS1NEndNData));
        assertEquals(1 + 1 + 0, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(2222, step2_2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 1, step2_1.getField(NS1Link).getFieldChangeListeners().size());        // +1
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 2, step1_1.getField(NEndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 2, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 3, end_1.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(NData));

        // Test changing values from top to bottom
        step3.set(NS2Link, step2_2);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));                                      // step2_2
        assertEquals(2222, step3.<Integer>get(NS2NS1NEndNData));                                    // 2222
        assertEquals(1 + 1 + 1, step2_2.getField(NS1Link).getFieldChangeListeners().size());        // +1
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(2222, step2_2.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step2_1.getField(NS1Link).getFieldChangeListeners().size());        // -1
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 0, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 2, step1_2.getField(NEndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_1.getField(NEndLink).getFieldChangeListeners().size());       // -1
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 1, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 3, end_2.getField(NData).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 2, end_1.getField(NData).getFieldChangeListeners().size());                // -1
        assertEquals(1111, end_1.<Integer>get(NData));

        step2_2.set(NS1Link, step1_3);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(3333, step3.<Integer>get(NS2NS1NEndNData));                                    // 3333
        assertEquals(1 + 1 + 1, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));                                    // step1_3
        assertEquals(3333, step2_2.<Integer>get(NS1NEndNData));                                     // 3333
        assertEquals(1 + 1 + 0, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 2, step1_3.getField(NEndLink).getFieldChangeListeners().size());       // +2
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(3333, step1_3.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 0, step1_2.getField(NEndLink).getFieldChangeListeners().size());       // -2
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 0, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 3, end_3.getField(NData).getFieldChangeListeners().size());                // +2
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 1, end_2.getField(NData).getFieldChangeListeners().size());                // -2
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 2, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        step1_3.set(NEndLink, end_4);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(4444, step3.<Integer>get(NS2NS1NEndNData));                                    // 4444
        assertEquals(1 + 1 + 1, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(4444, step2_2.<Integer>get(NS1NEndNData));                                     // 4444
        assertEquals(1 + 1 + 0, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 2, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));                                       // end_4
        assertEquals(4444, step1_3.<Integer>get(NEndNData));                                        // 4444
        assertEquals(1 + 1 + 0, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 3, end_4.getField(NData).getFieldChangeListeners().size());                // +3
        assertEquals(4444, end_4.<Integer>get(NData));
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());                // -3
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 1, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 2, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));

        end_4.set(NData, 444);
        assertEquals(1 + 1 + 0, step3.getField(NS2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(NS2NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<NullableMultiChain.Step2_N_TestObject>get(NS2Link));
        assertEquals(444, step3.<Integer>get(NS2NS1NEndNData));                                    // 444
        assertEquals(1 + 1 + 1, step2_2.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_2.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(444, step2_2.<Integer>get(NS1NEndNData));                                     // 444
        assertEquals(1 + 1 + 0, step2_1.getField(NS1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(NS1NEndNData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<NullableMultiChain.Step1_N_TestObject>get(NS1Link));
        assertEquals(1111, step2_1.<Integer>get(NS1NEndNData));
        assertEquals(1 + 1 + 2, step1_3.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_3.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(444, step1_3.<Integer>get(NEndNData));                                        // 444
        assertEquals(1 + 1 + 0, step1_2.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(2222, step1_2.<Integer>get(NEndNData));
        assertEquals(1 + 1 + 1, step1_1.getField(NEndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(NEndNData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<NullableMultiChain.End_N_TestObject>get(NEndLink));
        assertEquals(1111, step1_1.<Integer>get(NEndNData));
        assertEquals(1 + 3, end_4.getField(NData).getFieldChangeListeners().size());
        assertEquals(444, end_4.<Integer>get(NData));                                              // 444
        assertEquals(1 + 0, end_3.getField(NData).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(NData));
        assertEquals(1 + 1, end_2.getField(NData).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(NData));
        assertEquals(1 + 2, end_1.getField(NData).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(NData));
    }

    @SuppressWarnings({"PointlessArithmeticExpression"})
    @Test
    @Execution(CONCURRENT)
    void multiStepSource_BottomToTop() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(MultiChain.End_TestObject.class, MultiChain.Step1_TestObject.class, MultiChain.Step2_TestObject.class, MultiChain.Step3_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);

        MultiChain.End_TestObject end_1;
        MultiChain.End_TestObject end_2;
        MultiChain.End_TestObject end_3;
        MultiChain.End_TestObject end_4;
        MultiChain.Step1_TestObject step1_1;
        MultiChain.Step1_TestObject step1_2;
        MultiChain.Step1_TestObject step1_3;
        MultiChain.Step2_TestObject step2_1;
        MultiChain.Step2_TestObject step2_2;
        MultiChain.Step3_TestObject step3;

        // Check the end object is as expected before the test
        end_1 = new MultiChain.End_TestObject(1111, database).add();
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        end_2 = new MultiChain.End_TestObject(2222, database).add();
        assertEquals(1 + 0, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        end_3 = new MultiChain.End_TestObject(3333, database).add();
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 0, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        end_4 = new MultiChain.End_TestObject(4444, database).add();
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 0, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        // Check the step1 object is as expected before the test
        step1_1 = new MultiChain.Step1_TestObject(end_1, database).add();
        assertEquals(1 + 1 + 0, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 0, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 1, end_1.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(Data));

        step1_2 = new MultiChain.Step1_TestObject(end_2, database).add();
        assertEquals(1 + 1 + 0, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 1, end_2.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 1, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        step1_3 = new MultiChain.Step1_TestObject(end_3, database).add();
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 1, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 1, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        // Check the step2 object is as expected before the test
        step2_1 = new MultiChain.Step2_TestObject(step1_1, database).add();
        assertEquals(1 + 1 + 0, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_1.getField(EndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 1, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 2, end_1.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(Data));

        step2_2 = new MultiChain.Step2_TestObject(step1_2, database).add();
        assertEquals(1 + 1 + 0, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(2222, step2_2.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_2.getField(EndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 2, end_2.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 2, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        // Check the step3 object is as expected before the test
        step3 = new MultiChain.Step3_TestObject(step2_1, database).add();
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<MultiChain.Step2_TestObject>get(S2Link));
        assertEquals(1111, step3.<Integer>get(S2S1EndData));
        assertEquals(1 + 1 + 0, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(2222, step2_2.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 1, step2_1.getField(S1Link).getFieldChangeListeners().size());        // +1
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 2, step1_1.getField(EndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 2, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 3, end_1.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(Data));

        // Test changing values from bottom to top
        end_1.set(Data, 111);
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<MultiChain.Step2_TestObject>get(S2Link));
        assertEquals(111, step3.<Integer>get(S2S1EndData));                                     // 111
        assertEquals(1 + 1 + 0, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(2222, step2_2.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 1, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(111, step2_1.<Integer>get(S1EndData));                                      // 111
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 2, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(111, step1_1.<Integer>get(EndData));                                         // 111
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 2, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 3, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(111, end_1.<Integer>get(Data));                                               // 111

        step1_1.set(EndLink, end_4);
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<MultiChain.Step2_TestObject>get(S2Link));
        assertEquals(4444, step3.<Integer>get(S2S1EndData));                                    // 4444
        assertEquals(1 + 1 + 0, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(2222, step2_2.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 1, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(4444, step2_1.<Integer>get(S1EndData));                                     // 4444
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 2, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_1.<MultiChain.End_TestObject>get(EndLink));                                         // end_4
        assertEquals(4444, step1_1.<Integer>get(EndData));                                        // 4444
        assertEquals(1 + 3, end_4.getField(Data).getFieldChangeListeners().size());                // +3
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 2, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());                // -3
        assertEquals(111, end_1.<Integer>get(Data));

        step2_1.set(S1Link, step1_3);
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<MultiChain.Step2_TestObject>get(S2Link));
        assertEquals(3333, step3.<Integer>get(S2S1EndData));                                    // 3333
        assertEquals(1 + 1 + 0, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(2222, step2_2.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 1, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_1.<MultiChain.Step1_TestObject>get(S1Link));                                      // step1_3
        assertEquals(3333, step2_1.<Integer>get(S1EndData));                                     // 3333
        assertEquals(1 + 1 + 2, step1_3.getField(EndLink).getFieldChangeListeners().size());       // +2
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_1.getField(EndLink).getFieldChangeListeners().size());       // -2
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(4444, step1_1.<Integer>get(EndData));
        assertEquals(1 + 1, end_4.getField(Data).getFieldChangeListeners().size());                // -2
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 3, end_3.getField(Data).getFieldChangeListeners().size());                // +2
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 2, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(111, end_1.<Integer>get(Data));

        step3.set(S2Link, step2_2);
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<MultiChain.Step2_TestObject>get(S2Link));                                        // step2_2
        assertEquals(2222, step3.<Integer>get(S2S1EndData));                                    // 222
        assertEquals(1 + 1 + 1, step2_2.getField(S1Link).getFieldChangeListeners().size());        // +1
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(2222, step2_2.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step2_1.getField(S1Link).getFieldChangeListeners().size());        // -1
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(3333, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 1, step1_3.getField(EndLink).getFieldChangeListeners().size());       // -1
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 2, step1_2.getField(EndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(4444, step1_1.<Integer>get(EndData));
        assertEquals(1 + 1, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 2, end_3.getField(Data).getFieldChangeListeners().size());                // -1
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 3, end_2.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(111, end_1.<Integer>get(Data));
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    @Execution(CONCURRENT)
    void multiStepSource_TopToBottom() {
        List<Class<? extends DataObject>> knownTypes = Arrays.asList(MultiChain.End_TestObject.class, MultiChain.Step1_TestObject.class, MultiChain.Step2_TestObject.class, MultiChain.Step3_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);

        MultiChain.End_TestObject end_1;
        MultiChain.End_TestObject end_2;
        MultiChain.End_TestObject end_3;
        MultiChain.End_TestObject end_4;
        MultiChain.Step1_TestObject step1_1;
        MultiChain.Step1_TestObject step1_2;
        MultiChain.Step1_TestObject step1_3;
        MultiChain.Step2_TestObject step2_1;
        MultiChain.Step2_TestObject step2_2;
        MultiChain.Step3_TestObject step3;

        // Check the end object is as expected before the test
        end_1 = new MultiChain.End_TestObject(1111, database).add();
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        end_2 = new MultiChain.End_TestObject(2222, database).add();
        assertEquals(1 + 0, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        end_3 = new MultiChain.End_TestObject(3333, database).add();
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 0, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        end_4 = new MultiChain.End_TestObject(4444, database).add();
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 0, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 0, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        // Check the step1 object is as expected before the test
        step1_1 = new MultiChain.Step1_TestObject(end_1, database).add();
        assertEquals(1 + 1 + 0, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 0, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 1, end_1.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(Data));

        step1_2 = new MultiChain.Step1_TestObject(end_2, database).add();
        assertEquals(1 + 1 + 0, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 1, end_2.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 1, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        step1_3 = new MultiChain.Step1_TestObject(end_3, database).add();
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 1, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 1, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        // Check the step2 object is as expected before the test
        step2_1 = new MultiChain.Step2_TestObject(step1_1, database).add();
        assertEquals(1 + 1 + 0, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_1.getField(EndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 1, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 2, end_1.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(Data));

        step2_2 = new MultiChain.Step2_TestObject(step1_2, database).add();
        assertEquals(1 + 1 + 0, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(2222, step2_2.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_2.getField(EndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 2, end_2.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 2, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        // Check the step3 object is as expected before the test
        step3 = new MultiChain.Step3_TestObject(step2_1, database).add();
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_1, step3.<MultiChain.Step2_TestObject>get(S2Link));
        assertEquals(1111, step3.<Integer>get(S2S1EndData));
        assertEquals(1 + 1 + 0, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(2222, step2_2.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 1, step2_1.getField(S1Link).getFieldChangeListeners().size());        // +1
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 2, step1_1.getField(EndLink).getFieldChangeListeners().size());       // +1
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 2, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 3, end_1.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(1111, end_1.<Integer>get(Data));

        // Test changing values from bottom to top
        step3.set(S2Link, step2_2);
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<MultiChain.Step2_TestObject>get(S2Link));                                         // step2_2
        assertEquals(2222, step3.<Integer>get(S2S1EndData));                                        // 2222
        assertEquals(1 + 1 + 1, step2_2.getField(S1Link).getFieldChangeListeners().size());         // +1
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_2, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(2222, step2_2.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step2_1.getField(S1Link).getFieldChangeListeners().size());         // -1
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 0, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 2, step1_2.getField(EndLink).getFieldChangeListeners().size());        // +1
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_1.getField(EndLink).getFieldChangeListeners().size());        // -1
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 1, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 3, end_2.getField(Data).getFieldChangeListeners().size());                // +1
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 2, end_1.getField(Data).getFieldChangeListeners().size());                // -1
        assertEquals(1111, end_1.<Integer>get(Data));

        step2_2.set(S1Link, step1_3);
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<MultiChain.Step2_TestObject>get(S2Link));
        assertEquals(3333, step3.<Integer>get(S2S1EndData));                                    // 3333
        assertEquals(1 + 1 + 1, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_2.<MultiChain.Step1_TestObject>get(S1Link));                                    // step1_3
        assertEquals(3333, step2_2.<Integer>get(S1EndData));                                     // 3333
        assertEquals(1 + 1 + 0, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 2, step1_3.getField(EndLink).getFieldChangeListeners().size());       // +2
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_3, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(3333, step1_3.<Integer>get(EndData));
        assertEquals(1 + 1 + 0, step1_2.getField(EndLink).getFieldChangeListeners().size());       // -2
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 0, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 3, end_3.getField(Data).getFieldChangeListeners().size());                // +2
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 1, end_2.getField(Data).getFieldChangeListeners().size());                // -2
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 2, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        step1_3.set(EndLink, end_4);
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<MultiChain.Step2_TestObject>get(S2Link));
        assertEquals(4444, step3.<Integer>get(S2S1EndData));                                    // 4444
        assertEquals(1 + 1 + 1, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(4444, step2_2.<Integer>get(S1EndData));                                     // 4444
        assertEquals(1 + 1 + 0, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 2, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_3.<MultiChain.End_TestObject>get(EndLink));                                       // end_4
        assertEquals(4444, step1_3.<Integer>get(EndData));                                        // 4444
        assertEquals(1 + 1 + 0, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 3, end_4.getField(Data).getFieldChangeListeners().size());                // +3
        assertEquals(4444, end_4.<Integer>get(Data));
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());                // -3
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 1, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 2, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));

        end_4.set(Data, 444);
        assertEquals(1 + 1 + 0, step3.getField(S2Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step3.getField(S2S1EndData).getFieldChangeListeners().size());
        assertEquals(step2_2, step3.<MultiChain.Step2_TestObject>get(S2Link));
        assertEquals(444, step3.<Integer>get(S2S1EndData));                                    // 444
        assertEquals(1 + 1 + 1, step2_2.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_2.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_3, step2_2.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(444, step2_2.<Integer>get(S1EndData));                                     // 444
        assertEquals(1 + 1 + 0, step2_1.getField(S1Link).getFieldChangeListeners().size());
        assertEquals(1 + 0, step2_1.getField(S1EndData).getFieldChangeListeners().size());
        assertEquals(step1_1, step2_1.<MultiChain.Step1_TestObject>get(S1Link));
        assertEquals(1111, step2_1.<Integer>get(S1EndData));
        assertEquals(1 + 1 + 2, step1_3.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_3.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_4, step1_3.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(444, step1_3.<Integer>get(EndData));                                        // 444
        assertEquals(1 + 1 + 0, step1_2.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_2.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_2, step1_2.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(2222, step1_2.<Integer>get(EndData));
        assertEquals(1 + 1 + 1, step1_1.getField(EndLink).getFieldChangeListeners().size());
        assertEquals(1 + 0, step1_1.getField(EndData).getFieldChangeListeners().size());
        assertEquals(end_1, step1_1.<MultiChain.End_TestObject>get(EndLink));
        assertEquals(1111, step1_1.<Integer>get(EndData));
        assertEquals(1 + 3, end_4.getField(Data).getFieldChangeListeners().size());
        assertEquals(444, end_4.<Integer>get(Data));                                              // 444
        assertEquals(1 + 0, end_3.getField(Data).getFieldChangeListeners().size());
        assertEquals(3333, end_3.<Integer>get(Data));
        assertEquals(1 + 1, end_2.getField(Data).getFieldChangeListeners().size());
        assertEquals(2222, end_2.<Integer>get(Data));
        assertEquals(1 + 2, end_1.getField(Data).getFieldChangeListeners().size());
        assertEquals(1111, end_1.<Integer>get(Data));
    }
}
