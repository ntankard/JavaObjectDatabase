package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.end;

import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.SingleChain.SingleEnd_TestObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.DatabaseFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.util.Collections;
import java.util.List;

import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.SingleChain.SingleEnd_TestObject.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class End_SourceTest {

    // TODO check for full coverage

    @BeforeAll
    static void beforeAll() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    /**
     * Test that a field using a single End_Source (no Step_Source) updates correctly in all cases.
     */
    @Test
    @Execution(CONCURRENT)
    void endSourceOnly() {
        List<Class<? extends DataObject>> knownTypes = Collections.singletonList(SingleEnd_TestObject.class);
        Database database = DatabaseFactory.getEmptyDatabase(knownTypes);
        SingleEnd_TestObject tO;

        /*
         * Editable field, can't be null
         */

        // Check that the value is correct when created
        tO = new SingleEnd_TestObject(5, database);
        assertEquals(5, tO.<Integer>get(CoreData));
        assertEquals(5, tO.<Integer>get(CoreData_Derived1));
        assertEquals(5, tO.<Integer>get(CoreData_Derived2));

        // Check that the value is correct when changed
        tO.set(CoreData, 15);
        assertEquals(15, tO.<Integer>get(CoreData));
        assertEquals(15, tO.<Integer>get(CoreData_Derived1));
        assertEquals(15, tO.<Integer>get(CoreData_Derived2));

        /*
         * Editable field, can be null, starts as null
         */

        // Check that the value is correct when created
        tO = new SingleEnd_TestObject(5, database);
        assertNull(tO.get(NullableCoreData));
        assertNull(tO.get(NullableCoreData_Derived1));
        assertNull(tO.get(NullableCoreData_Derived2));

        // Check that the value is correct when changed to not null
        tO.set(NullableCoreData, 20);
        assertEquals(20, tO.<Integer>get(NullableCoreData));
        assertEquals(20, tO.<Integer>get(NullableCoreData_Derived1));
        assertEquals(20, tO.<Integer>get(NullableCoreData_Derived2));

        // Check that the value is correct when changed back to null
        tO.set(NullableCoreData, null);
        assertNull(tO.get(NullableCoreData));
        assertNull(tO.get(NullableCoreData_Derived1));
        assertNull(tO.get(NullableCoreData_Derived2));

        /*
         * Editable field, can be null, starts as not null
         */

        // Check that the value is correct when created
        tO = new SingleEnd_TestObject(-1, 100, database);
        assertEquals(100, tO.<Integer>get(NullableCoreData));
        assertEquals(100, tO.<Integer>get(NullableCoreData_Derived1));
        assertEquals(100, tO.<Integer>get(NullableCoreData_Derived2));

        // Check that the value is correct when changed to null
        tO.set(NullableCoreData, null);
        assertNull(tO.get(NullableCoreData));
        assertNull(tO.get(NullableCoreData_Derived1));
        assertNull(tO.get(NullableCoreData_Derived2));

        // Check that the value is correct when changed back to a value
        tO.set(NullableCoreData, 200);
        assertEquals(200, tO.<Integer>get(NullableCoreData));
        assertEquals(200, tO.<Integer>get(NullableCoreData_Derived1));
        assertEquals(200, tO.<Integer>get(NullableCoreData_Derived2));

        /*
         * Static field
         */

        // Check that the value is correct when created, both for field that are added before and after the static value gets set
        assertEquals(10, tO.<Integer>get(StaticData_Derived1));
        assertEquals(10, tO.<Integer>get(StaticData));
        assertEquals(10, tO.<Integer>get(StaticData_Derived2));
        assertEquals(10, tO.<Integer>get(StaticData_Derived3));

        /*
         * Static field, null
         */

        // Check that the value is correct when created, both for field that are added before and after the static value gets set
        assertNull(tO.get(NullableStaticData));
        assertNull(tO.get(NullableStaticData_Derived1));
        assertNull(tO.get(NullableStaticData_Derived2));
        assertNull(tO.get(NullableStaticData_Derived3));
    }
}
