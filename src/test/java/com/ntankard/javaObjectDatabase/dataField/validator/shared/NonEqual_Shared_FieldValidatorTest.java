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
                new NonEqual_Shared_FieldValidator("", ""));
    }

    @Test
    @Execution(CONCURRENT)
    void dataObject_construction() {
        Database database = DatabaseFactory.getEmptyDatabase(Collections.singletonList(NonEqual_SharedValidator_TestObject.class));

        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(7, 7, 10, 11, database));
        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(4, 4, 10, 11, database));
        assertThrows(NonCorruptingException.class, () -> new NonEqual_SharedValidator_TestObject(-345, -345, 10, 11, database));
        assertThrows(NonCorruptingException.class,() -> new NonEqual_SharedValidator_TestObject(24, 345, null, null, database));

        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(24, 345, 10, 11, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(123, 34, 10, 11, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(-23423, 675, 10, 11, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(null, 34, 10, 11, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(-23423, null, 10, 11, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(null, null, 10, 11, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(24, 345, null, 11, database));
        assertDoesNotThrow(() -> new NonEqual_SharedValidator_TestObject(24, 345, 10, null, database));
    }

    @Test
    @Execution(CONCURRENT)
    void dataObject_setter() {
        Database database = DatabaseFactory.getEmptyDatabase(Collections.singletonList(NonEqual_SharedValidator_TestObject.class));
        NonEqual_SharedValidator_TestObject sharedValidatorTestObject = new NonEqual_SharedValidator_TestObject(-10, 10, 10, 11, database);

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
        assertThrows(NonCorruptingException.class,() -> sharedValidatorTestObject.set(Second_NullForbidden, null));
    }
}
