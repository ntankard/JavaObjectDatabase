package com.ntankard.javaObjectDatabase.dataField.validator.shared;

import com.ntankard.javaObjectDatabase.dataField.validator.testObjects.NonEqual_SharedValidator_TestObject;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.exception.nonCorrupting.NonCorruptingException;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.DatabaseFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.util.Collections;

import static com.ntankard.javaObjectDatabase.dataField.validator.testObjects.NonEqual_SharedValidator_TestObject.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class NonEqual_Shared_FieldValidatorTest {

    @BeforeAll
    static void beforeAll() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Test
    @Execution(CONCURRENT)
    void constructor() {
        assertThrows(AssertionError.class, () ->
                new NonEqual_Shared_FieldValidator(null, ""));

        assertThrows(AssertionError.class, () ->
                new NonEqual_Shared_FieldValidator("", null));

        assertDoesNotThrow(() ->
                new NonEqual_Shared_FieldValidator("test1", "test2"));
    }

    @Test
    @Execution(CONCURRENT)
    void dataObject_construction() {
        Database database = DatabaseFactory.getEmptyDatabase(Collections.singletonList(NonEqual_SharedValidator_TestObject.class));

        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(7, 7, 8, 10, 11, 12, database));
        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(6, 7, 7, 10, 11, 12, database));
        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(7, 7, 7, 10, 11, 12, database));
        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(6, 7, 8, 10, 10, 12, database));
        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(6, 7, 8, 10, 10, 12, database));
        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(6, 7, 8, 10, 11, 11, database));
        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(6, 7, 8, 10, 10, 10, database));

        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(24, 345, 321, 10, 11, 12, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(123, 34, 3424, 10, 11, 12, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(-23423, 675, 234, 10, 11, 12, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(-23423, 675, null, 10, 11, 12, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(-23423, null, 234, 10, 11, 12, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(null, 675, 234, 10, 11, 12, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(null, null, 234, 10, 11, 12, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(-23423, null, null, 10, 11, 12, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(null, null, null, 10, 11, 12, database));
    }

    @Test
    @Execution(CONCURRENT)
    void dataObject_setter() {
        Database database = DatabaseFactory.getEmptyDatabase(Collections.singletonList(NonEqual_SharedValidator_TestObject.class));
        NonEqual_SharedValidator_TestObject sharedValidatorTestObject = new NonEqual_SharedValidator_TestObject(-10, 10, 1, 10, 11, 12, database);

        assertThrows(NonCorruptingException.class, () -> sharedValidatorTestObject.set(First, 10));
        assertDoesNotThrow(() -> sharedValidatorTestObject.set(First, -20));
        assertDoesNotThrow(() -> sharedValidatorTestObject.set(First, -10));
        assertThrows(NonCorruptingException.class, () -> sharedValidatorTestObject.set(First, 10));

        assertDoesNotThrow(() -> sharedValidatorTestObject.set(Second, 20));

        assertDoesNotThrow(() -> sharedValidatorTestObject.set(First, 10));
        assertThrows(NonCorruptingException.class, () -> sharedValidatorTestObject.set(First, 20));
        assertDoesNotThrow(() -> sharedValidatorTestObject.set(First, -10));

        assertDoesNotThrow(() -> sharedValidatorTestObject.set(Second, 10));

        assertThrows(NonCorruptingException.class, () -> sharedValidatorTestObject.set(Second, -10));
        assertDoesNotThrow(() -> sharedValidatorTestObject.set(Second, 20));
        assertDoesNotThrow(() -> sharedValidatorTestObject.set(Second, 10));
        assertThrows(NonCorruptingException.class, () -> sharedValidatorTestObject.set(Second, -10));

        assertDoesNotThrow(() -> sharedValidatorTestObject.set(First, -20));

        assertDoesNotThrow(() -> sharedValidatorTestObject.set(Second, -10));
        assertThrows(NonCorruptingException.class, () -> sharedValidatorTestObject.set(Second, -20));
        assertDoesNotThrow(() -> sharedValidatorTestObject.set(Second, 10));

        assertDoesNotThrow(() -> sharedValidatorTestObject.set(First, null));
        assertDoesNotThrow(() -> sharedValidatorTestObject.set(Second, null));
        assertDoesNotThrow(() -> sharedValidatorTestObject.set(First_NullForbidden, null));
        assertThrows(NonCorruptingException.class, () -> sharedValidatorTestObject.set(Second_NullForbidden, null));
    }
}
