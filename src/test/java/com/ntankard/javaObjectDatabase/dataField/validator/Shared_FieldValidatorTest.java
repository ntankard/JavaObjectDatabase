package com.ntankard.javaObjectDatabase.dataField.validator;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.validator.Shared_FieldValidator.OnlyNewSharedValidator;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.exception.corrupting.DatabaseStructureException;
import com.ntankard.javaObjectDatabase.exception.nonCorrupting.NonCorruptingException;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.BlankTestDataObject;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.DatabaseFactory;
import com.ntankard.javaObjectDatabase.dataField.validator.testObjects.SharedValidator_TestObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.util.Collections;

import static com.ntankard.javaObjectDatabase.dataField.validator.testObjects.SharedValidator_TestObject.First;
import static com.ntankard.javaObjectDatabase.dataField.validator.testObjects.SharedValidator_TestObject.Second;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class Shared_FieldValidatorTest {

    @BeforeAll
    static void beforeAll() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Test
    @Execution(CONCURRENT)
    void constructor() {
        assertThrows(AssertionError.class, () ->
                new Shared_FieldValidator<>(null, "",
                        (firstNewValue, firstPastValue, secondNewValue, secondPastValue, container) ->
                                true, ""));

        assertThrows(AssertionError.class, () ->
                new Shared_FieldValidator<>("", null,
                        (firstNewValue, firstPastValue, secondNewValue, secondPastValue, container) ->
                                true, ""));

        assertThrows(AssertionError.class, () ->
                new Shared_FieldValidator<>(null, "",
                        (firstNewValue, secondNewValue, container) ->
                                true, ""));

        assertThrows(AssertionError.class, () ->
                new Shared_FieldValidator<>("", null,
                        (firstNewValue, secondNewValue, container) ->
                                true, ""));

        assertThrows(AssertionError.class, () ->
                new Shared_FieldValidator<>(null, null,
                        (firstNewValue, secondNewValue, container) ->
                                true, ""));

        assertThrows(AssertionError.class, () ->
                new Shared_FieldValidator<>(null, null,
                        (firstNewValue, secondNewValue, container) ->
                                true, ""));

        assertDoesNotThrow(() ->
                new Shared_FieldValidator<>("", "",
                        (firstNewValue, secondNewValue, container) ->
                                true, ""));

        assertDoesNotThrow(() ->
                new Shared_FieldValidator<>("", "",
                        (firstNewValue, secondNewValue, container) ->
                                true, ""));
    }

    @Test
    @Execution(CONCURRENT)
    void dataObject_construction() {
        Database database = DatabaseFactory.getEmptyDatabase(Collections.singletonList(SharedValidator_TestObject.class));

        assertThrows(NonCorruptingException.class, () -> new SharedValidator_TestObject(7, 7, database));
        assertThrows(NonCorruptingException.class, () -> new SharedValidator_TestObject(4, 4, database));
        assertThrows(NonCorruptingException.class, () -> new SharedValidator_TestObject(-345, -345, database));
        assertDoesNotThrow(() -> new SharedValidator_TestObject(24, 345, database));
        assertDoesNotThrow(() -> new SharedValidator_TestObject(123, 34, database));
        assertDoesNotThrow(() -> new SharedValidator_TestObject(-23423, 675, database));
    }

    @Test
    @Execution(CONCURRENT)
    void dataObject_setter() {
        Database database = DatabaseFactory.getEmptyDatabase(Collections.singletonList(SharedValidator_TestObject.class));
        SharedValidator_TestObject sharedValidatorTestObject = new SharedValidator_TestObject(-10, 10, database);

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
    }

    @Test
    @Execution(CONCURRENT)
    void attach() {
        Shared_FieldValidator<Integer, Integer, ?> sharedFieldValidator =
                new Shared_FieldValidator<>(
                        First,
                        Second,
                        (firstNewValue, secondNewValue, container) ->
                                !firstNewValue.equals(secondNewValue), "");

        DataObject_Schema dataObjectSchema1 = DataObject.getDataObjectSchema();
        dataObjectSchema1.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema1.add(new DataField_Schema<>(Second, Integer.class));
        dataObjectSchema1.<Integer>get(First).addValidator(sharedFieldValidator.getFirstFilter());
        dataObjectSchema1.<Integer>get(Second).addValidator(sharedFieldValidator.getSecondFilter());
        assertDoesNotThrow(() -> dataObjectSchema1.finaliseContainer(SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema2 = DataObject.getDataObjectSchema();
        dataObjectSchema2.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema2.add(new DataField_Schema<>(Second, Integer.class));
        dataObjectSchema2.<Integer>get(First).addValidator(sharedFieldValidator.getFirstFilter());
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema2.finaliseContainer(SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema3 = DataObject.getDataObjectSchema();
        dataObjectSchema3.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema3.add(new DataField_Schema<>(Second, Integer.class));
        dataObjectSchema3.<Integer>get(Second).addValidator(sharedFieldValidator.getSecondFilter());
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema3.finaliseContainer(SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema4 = DataObject.getDataObjectSchema();
        dataObjectSchema4.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema4.add(new DataField_Schema<>(Second, Integer.class));
        assertDoesNotThrow(() -> dataObjectSchema4.finaliseContainer(SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema5 = DataObject.getDataObjectSchema();
        dataObjectSchema5.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema5.<Integer>get(First).addValidator(sharedFieldValidator.getFirstFilter());
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema5.finaliseContainer(SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema6 = DataObject.getDataObjectSchema();
        dataObjectSchema6.add(new DataField_Schema<>(Second, Integer.class));
        dataObjectSchema6.<Integer>get(Second).addValidator(sharedFieldValidator.getSecondFilter());
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema6.finaliseContainer(SharedValidator_TestObject.class));
    }

    @Test
    @Execution(CONCURRENT)
    void internalBehavior() {
        Test_OnlyNewSharedValidator test_onlyNewSharedValidator = new Test_OnlyNewSharedValidator();
        Shared_FieldValidator<Integer, Integer, BlankTestDataObject> testSharedFieldValidator =
                new Shared_FieldValidator<>(
                        First,
                        Second,
                        test_onlyNewSharedValidator, "");

        // Create the test schema
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        Test_DataField_Schema firstFieldSchema = new Test_DataField_Schema(First);
        firstFieldSchema.addValidator(testSharedFieldValidator.getFirstFilter());
        dataObjectSchema.add(firstFieldSchema);

        Test_DataField_Schema secondFieldSchema = new Test_DataField_Schema(Second);
        secondFieldSchema.addValidator(testSharedFieldValidator.getSecondFilter());
        dataObjectSchema.add(secondFieldSchema);

        dataObjectSchema.finaliseContainer(BlankTestDataObject.class);

        // Create the test instance
        BlankTestDataObject blankTestDataObject = new BlankTestDataObject(dataObjectSchema);
        blankTestDataObject.setAllValues(null
                , First, 1
                , Second, 1);

        // When both are not ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = false;

        // Test that both do not call the validator
        assertTrue(testSharedFieldValidator.getFirstFilter().isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getFirstFilter().isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // When only first is ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = false;

        // Test that the fist does not call validate because the second is not ready
        assertTrue(testSharedFieldValidator.getFirstFilter().isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // Test that the second does call validate because the first is ready
        assertFalse(testSharedFieldValidator.getSecondFilter().isValid(null, null, blankTestDataObject));
        assertEquals(1, test_onlyNewSharedValidator.callCount);

        // When only second is ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = true;

        // Test that the second does not call validate because the first is not ready
        assertTrue(testSharedFieldValidator.getSecondFilter().isValid(null, null, blankTestDataObject));
        assertEquals(1, test_onlyNewSharedValidator.callCount);

        // Test that the first does call validate because the second is ready
        assertFalse(testSharedFieldValidator.getFirstFilter().isValid(null, null, blankTestDataObject));
        assertEquals(2, test_onlyNewSharedValidator.callCount);

        // Check that the validator is called when both are ready
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = true;
        assertFalse(testSharedFieldValidator.getFirstFilter().isValid(null, null, blankTestDataObject));
        assertEquals(3, test_onlyNewSharedValidator.callCount);
        assertFalse(testSharedFieldValidator.getSecondFilter().isValid(null, null, blankTestDataObject));
        assertEquals(4, test_onlyNewSharedValidator.callCount);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Test Objects ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * A DataField that can have is hasValidValue() method response external set
     */
    static class Test_DataField extends DataField<Integer> {

        public boolean hasValidValue;

        public Test_DataField(DataField_Schema<Integer> dataFieldSchema, DataObject container) {
            super(dataFieldSchema, container);
        }

        @Override
        public boolean hasValidValue() {
            return hasValidValue;
        }
    }

    /**
     * A DataField_Schema that will produce a Test_DataField instead of a DataField when calling generate
     */
    static class Test_DataField_Schema extends DataField_Schema<Integer> {

        public Test_DataField_Schema(String identifierName) {
            super(identifierName, Integer.class, true);
        }

        @Override
        public DataField<Integer> generate(DataObject container) {
            return new Test_DataField(this, container);
        }
    }

    /**
     * A OnlyNewSharedValidator that can report when validate is called and will always return false
     */
    static class Test_OnlyNewSharedValidator implements OnlyNewSharedValidator<Integer, Integer, BlankTestDataObject> {

        public int callCount = 0;

        @Override
        public boolean validate(Integer firstNewValue, Integer secondNewValue, BlankTestDataObject container) {
            callCount++;
            return false;
        }
    }
}
