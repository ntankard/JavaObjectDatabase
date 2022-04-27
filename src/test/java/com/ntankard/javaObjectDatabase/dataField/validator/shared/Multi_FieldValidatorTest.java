package com.ntankard.javaObjectDatabase.dataField.validator.shared;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.validator.testObjects.NonEqual_SharedValidator_TestObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.exception.corrupting.DatabaseStructureException;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.BlankTestDataObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import static com.ntankard.javaObjectDatabase.dataField.validator.testObjects.NonEqual_SharedValidator_TestObject.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

class Multi_FieldValidatorTest {

    @BeforeAll
    static void beforeAll() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Test
    @Execution(CONCURRENT)
    void constructor() {
        assertThrows(AssertionError.class, () -> new Multi_FieldValidator<>(null, "test", "test"));
        assertThrows(AssertionError.class, () -> new Multi_FieldValidator<>((newValues, pastValues, container) -> true, "test"));
        assertThrows(AssertionError.class, () -> new Multi_FieldValidator<>((newValues, pastValues, container) -> true, "test", "test", "test"));
        assertDoesNotThrow(() -> new Multi_FieldValidator<>((newValues, pastValues, container) -> true, "test", "test"));
        assertDoesNotThrow(() -> new Multi_FieldValidator<>((newValues, pastValues, container) -> true, "test", "test", "test2"));
        assertDoesNotThrow(() -> new Multi_FieldValidator<>((newValues, pastValues, container) -> true, "test", "test", "test2", "test3"));
    }

    @Test
    @Execution(CONCURRENT)
    void attach() {
        Multi_FieldValidator<?> sharedFieldValidator =
                new Multi_FieldValidator<>(
                        (newValues, pastValues, container) ->
                                !newValues[0].equals(newValues[1]), "", First,
                        Second
                );

        DataObject_Schema dataObjectSchema1 = DataObject.getDataObjectSchema();
        dataObjectSchema1.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema1.add(new DataField_Schema<>(Second, Integer.class));
        dataObjectSchema1.get(First).addValidator(sharedFieldValidator.getValidator(First));
        dataObjectSchema1.get(Second).addValidator(sharedFieldValidator.getValidator(Second));
        assertDoesNotThrow(() -> dataObjectSchema1.finaliseContainer(NonEqual_SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema2 = DataObject.getDataObjectSchema();
        dataObjectSchema2.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema2.add(new DataField_Schema<>(Second, Integer.class));
        dataObjectSchema2.get(First).addValidator(sharedFieldValidator.getValidator(First));
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema2.finaliseContainer(NonEqual_SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema3 = DataObject.getDataObjectSchema();
        dataObjectSchema3.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema3.add(new DataField_Schema<>(Second, Integer.class));
        dataObjectSchema3.get(Second).addValidator(sharedFieldValidator.getValidator(Second));
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema3.finaliseContainer(NonEqual_SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema4 = DataObject.getDataObjectSchema();
        dataObjectSchema4.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema4.add(new DataField_Schema<>(Second, Integer.class));
        assertDoesNotThrow(() -> dataObjectSchema4.finaliseContainer(NonEqual_SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema5 = DataObject.getDataObjectSchema();
        dataObjectSchema5.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema5.get(First).addValidator(sharedFieldValidator.getValidator(First));
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema5.finaliseContainer(NonEqual_SharedValidator_TestObject.class));

        DataObject_Schema dataObjectSchema6 = DataObject.getDataObjectSchema();
        dataObjectSchema6.add(new DataField_Schema<>(Second, Integer.class));
        dataObjectSchema6.get(Second).addValidator(sharedFieldValidator.getValidator(Second));
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema6.finaliseContainer(NonEqual_SharedValidator_TestObject.class));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    @Execution(CONCURRENT)
    void internalBehavior() {
        Test_OnlyNewSharedValidator test_onlyNewSharedValidator = new Test_OnlyNewSharedValidator();
        Multi_FieldValidator<BlankTestDataObject> testSharedFieldValidator =
                new Multi_FieldValidator<>(
                        test_onlyNewSharedValidator, "", First,
                        Second, Third
                );

        // Create the test schema
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        Test_DataField_Schema firstFieldSchema = new Test_DataField_Schema(First);
        ((DataField_Schema) firstFieldSchema).addValidator(testSharedFieldValidator.getValidator(First));
        dataObjectSchema.add(firstFieldSchema);

        Test_DataField_Schema secondFieldSchema = new Test_DataField_Schema(Second);
        ((DataField_Schema) secondFieldSchema).addValidator(testSharedFieldValidator.getValidator(Second));
        dataObjectSchema.add(secondFieldSchema);

        Test_DataField_Schema thirdFieldSchema = new Test_DataField_Schema(Third);
        ((DataField_Schema) thirdFieldSchema).addValidator(testSharedFieldValidator.getValidator(Third));
        dataObjectSchema.add(thirdFieldSchema);

        dataObjectSchema.finaliseContainer(BlankTestDataObject.class);

        // Create the test instance
        BlankTestDataObject blankTestDataObject = new BlankTestDataObject(dataObjectSchema);
        blankTestDataObject.setAllValues(First, 1
                , Second, 2
                , Third, 3);

        // When all are not ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Third)).hasValidValue = false;

        // Test that all do not call the validator
        assertTrue(testSharedFieldValidator.getValidator(First).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getValidator(Second).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getValidator(Third).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // When only first is ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Third)).hasValidValue = false;

        // Test that all do not call the validator
        assertTrue(testSharedFieldValidator.getValidator(First).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getValidator(Second).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getValidator(Third).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // When only second is ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Third)).hasValidValue = false;

        // Test that all do not call the validator
        assertTrue(testSharedFieldValidator.getValidator(First).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getValidator(Second).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getValidator(Third).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // When only third is ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Third)).hasValidValue = true;

        // Test that all do not call the validator
        assertTrue(testSharedFieldValidator.getValidator(First).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getValidator(Second).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getValidator(Third).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // When only third is not ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Third)).hasValidValue = false;

        // Test that the fist does not call validate because the third is not ready
        assertTrue(testSharedFieldValidator.getValidator(First).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // Test that the second does not call validate because the third is not ready
        assertTrue(testSharedFieldValidator.getValidator(Second).isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // Test that the second does call validate because the others are ready
        assertFalse(testSharedFieldValidator.getValidator(Third).isValid(null, null, blankTestDataObject));
        assertEquals(1, test_onlyNewSharedValidator.callCount);

        // When only second is not ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Third)).hasValidValue = true;

        // Test that the fist does not call validate because the second is not ready
        assertTrue(testSharedFieldValidator.getValidator(First).isValid(null, null, blankTestDataObject));
        assertEquals(1, test_onlyNewSharedValidator.callCount);

        // Test that the second does call validate because the second is not ready
        assertTrue(testSharedFieldValidator.getValidator(Third).isValid(null, null, blankTestDataObject));
        assertEquals(1, test_onlyNewSharedValidator.callCount);

        // Test that the second does not call validate because the others are ready
        assertFalse(testSharedFieldValidator.getValidator(Second).isValid(null, null, blankTestDataObject));
        assertEquals(2, test_onlyNewSharedValidator.callCount);

        // When only first is not ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Third)).hasValidValue = true;

        // Test that the second does not call validate because the first is not ready
        assertTrue(testSharedFieldValidator.getValidator(Second).isValid(null, null, blankTestDataObject));
        assertEquals(2, test_onlyNewSharedValidator.callCount);

        // Test that the second does call validate because the first is not ready
        assertTrue(testSharedFieldValidator.getValidator(Third).isValid(null, null, blankTestDataObject));
        assertEquals(2, test_onlyNewSharedValidator.callCount);

        // Test that the fist does not call validate because the others are ready
        assertFalse(testSharedFieldValidator.getValidator(First).isValid(null, null, blankTestDataObject));
        assertEquals(3, test_onlyNewSharedValidator.callCount);

        // Check that the validator is called when both are ready
        ((Test_DataField) blankTestDataObject.<Integer>getField(First)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Second)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(Third)).hasValidValue = true;
        assertFalse(testSharedFieldValidator.getValidator(First).isValid(null, null, blankTestDataObject));
        assertEquals(4, test_onlyNewSharedValidator.callCount);
        assertFalse(testSharedFieldValidator.getValidator(Second).isValid(null, null, blankTestDataObject));
        assertEquals(5, test_onlyNewSharedValidator.callCount);
        assertFalse(testSharedFieldValidator.getValidator(Second).isValid(null, null, blankTestDataObject));
        assertEquals(6, test_onlyNewSharedValidator.callCount);
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
    static class Test_OnlyNewSharedValidator implements Multi_FieldValidator.MultiValidator<BlankTestDataObject> {

        public int callCount = 0;

        @Override
        public boolean validate(Object[] newValues, Object[] pastValues, BlankTestDataObject container) {
            callCount++;
            return false;
        }
    }
}
