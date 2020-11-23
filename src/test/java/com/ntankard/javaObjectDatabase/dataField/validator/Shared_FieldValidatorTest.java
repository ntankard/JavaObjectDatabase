package com.ntankard.javaObjectDatabase.dataField.validator;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.validator.Shared_FieldValidator.OnlyNewSharedValidator;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.exception.corrupting.DatabaseStructureException;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.BlankTestDataObject;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.DatabaseFactory;
import com.ntankard.javaObjectDatabase.testUtil.testDatabases.GeneralTestDataObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.util.Collections;

import static com.ntankard.javaObjectDatabase.testUtil.testDatabases.GeneralTestDataObject.GeneralTestDataObject_SharedValidator_First;
import static com.ntankard.javaObjectDatabase.testUtil.testDatabases.GeneralTestDataObject.GeneralTestDataObject_SharedValidator_Second;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Execution(CONCURRENT)
class Shared_FieldValidatorTest {

    @Test
    @Execution(CONCURRENT)
    void constructor() {
        assertThrows(IllegalArgumentException.class, () ->
                new Shared_FieldValidator<>(null, "",
                        (firstNewValue, firstPastValue, secondNewValue, secondPastValue, container) ->
                                true));

        assertThrows(IllegalArgumentException.class, () ->
                new Shared_FieldValidator<>("", null,
                        (firstNewValue, firstPastValue, secondNewValue, secondPastValue, container) ->
                                true));

        assertThrows(IllegalArgumentException.class, () ->
                new Shared_FieldValidator<>(null, "",
                        (firstNewValue, secondNewValue, container) ->
                                true));

        assertThrows(IllegalArgumentException.class, () ->
                new Shared_FieldValidator<>("", null,
                        (firstNewValue, secondNewValue, container) ->
                                true));

        assertThrows(IllegalArgumentException.class, () ->
                new Shared_FieldValidator<>(null, null,
                        (firstNewValue, secondNewValue, container) ->
                                true));

        assertThrows(IllegalArgumentException.class, () ->
                new Shared_FieldValidator<>(null, null,
                        (firstNewValue, secondNewValue, container) ->
                                true));

        assertDoesNotThrow(() ->
                new Shared_FieldValidator<>("", "",
                        (firstNewValue, secondNewValue, container) ->
                                true));

        assertDoesNotThrow(() ->
                new Shared_FieldValidator<>("", "",
                        (firstNewValue, secondNewValue, container) ->
                                true));
    }

    @Test
    @Execution(CONCURRENT)
    void dataObject_construction() {
        Database database = DatabaseFactory.getEmptyDatabase(Collections.singletonList(GeneralTestDataObject.class));

        assertThrows(IllegalArgumentException.class, () -> new GeneralTestDataObject(7, 7, database));
        assertThrows(IllegalArgumentException.class, () -> new GeneralTestDataObject(4, 4, database));
        assertThrows(IllegalArgumentException.class, () -> new GeneralTestDataObject(-345, -345, database));
        assertDoesNotThrow(() -> new GeneralTestDataObject(24, 345, database));
        assertDoesNotThrow(() -> new GeneralTestDataObject(123, 34, database));
        assertDoesNotThrow(() -> new GeneralTestDataObject(-23423, 675, database));
    }

    @Test
    @Execution(CONCURRENT)
    void dataObject_setter() {
        Database database = DatabaseFactory.getEmptyDatabase(Collections.singletonList(GeneralTestDataObject.class));
        GeneralTestDataObject generalTestDataObject = new GeneralTestDataObject(-10, 10, database);

        assertThrows(IllegalArgumentException.class, () -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_First, 10));
        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_First, -20));
        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_First, -10));
        assertThrows(IllegalArgumentException.class, () -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_First, 10));

        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_Second, 20));

        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_First, 10));
        assertThrows(IllegalArgumentException.class, () -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_First, 20));
        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_First, -10));

        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_Second, 10));

        assertThrows(IllegalArgumentException.class, () -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_Second, -10));
        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_Second, 20));
        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_Second, 10));
        assertThrows(IllegalArgumentException.class, () -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_Second, -10));

        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_First, -20));

        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_Second, -10));
        assertThrows(IllegalArgumentException.class, () -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_Second, -20));
        assertDoesNotThrow(() -> generalTestDataObject.set(GeneralTestDataObject_SharedValidator_Second, 10));
    }

    @Test
    @Execution(CONCURRENT)
    void attach() {
        Shared_FieldValidator<Integer, Integer, ?> sharedFieldValidator =
                new Shared_FieldValidator<>(
                        GeneralTestDataObject_SharedValidator_First,
                        GeneralTestDataObject_SharedValidator_Second,
                        (firstNewValue, secondNewValue, container) ->
                                !firstNewValue.equals(secondNewValue));

        DataObject_Schema dataObjectSchema1 = DataObject.getDataObjectSchema();
        dataObjectSchema1.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_First, Integer.class));
        dataObjectSchema1.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_Second, Integer.class));
        dataObjectSchema1.<Integer>get(GeneralTestDataObject_SharedValidator_First).addValidator(sharedFieldValidator.getFirstFilter());
        dataObjectSchema1.<Integer>get(GeneralTestDataObject_SharedValidator_Second).addValidator(sharedFieldValidator.getSecondFilter());
        assertDoesNotThrow(() -> dataObjectSchema1.finaliseContainer(GeneralTestDataObject.class));

        DataObject_Schema dataObjectSchema2 = DataObject.getDataObjectSchema();
        dataObjectSchema2.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_First, Integer.class));
        dataObjectSchema2.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_Second, Integer.class));
        dataObjectSchema2.<Integer>get(GeneralTestDataObject_SharedValidator_First).addValidator(sharedFieldValidator.getFirstFilter());
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema2.finaliseContainer(GeneralTestDataObject.class));

        DataObject_Schema dataObjectSchema3 = DataObject.getDataObjectSchema();
        dataObjectSchema3.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_First, Integer.class));
        dataObjectSchema3.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_Second, Integer.class));
        dataObjectSchema3.<Integer>get(GeneralTestDataObject_SharedValidator_Second).addValidator(sharedFieldValidator.getSecondFilter());
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema3.finaliseContainer(GeneralTestDataObject.class));

        DataObject_Schema dataObjectSchema4 = DataObject.getDataObjectSchema();
        dataObjectSchema4.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_First, Integer.class));
        dataObjectSchema4.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_Second, Integer.class));
        assertDoesNotThrow(() -> dataObjectSchema4.finaliseContainer(GeneralTestDataObject.class));

        DataObject_Schema dataObjectSchema5 = DataObject.getDataObjectSchema();
        dataObjectSchema5.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_First, Integer.class));
        dataObjectSchema5.<Integer>get(GeneralTestDataObject_SharedValidator_First).addValidator(sharedFieldValidator.getFirstFilter());
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema5.finaliseContainer(GeneralTestDataObject.class));

        DataObject_Schema dataObjectSchema6 = DataObject.getDataObjectSchema();
        dataObjectSchema6.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_Second, Integer.class));
        dataObjectSchema6.<Integer>get(GeneralTestDataObject_SharedValidator_Second).addValidator(sharedFieldValidator.getSecondFilter());
        assertThrows(DatabaseStructureException.class, () -> dataObjectSchema6.finaliseContainer(GeneralTestDataObject.class));
    }

    @Test
    @Execution(CONCURRENT)
    void internalBehavior() {
        Test_OnlyNewSharedValidator test_onlyNewSharedValidator = new Test_OnlyNewSharedValidator();
        Shared_FieldValidator<Integer, Integer, BlankTestDataObject> testSharedFieldValidator =
                new Shared_FieldValidator<>(
                        GeneralTestDataObject_SharedValidator_First,
                        GeneralTestDataObject_SharedValidator_Second,
                        test_onlyNewSharedValidator);

        // Create the test schema
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        Test_DataField_Schema firstFieldSchema = new Test_DataField_Schema(GeneralTestDataObject_SharedValidator_First);
        firstFieldSchema.addValidator(testSharedFieldValidator.getFirstFilter());
        dataObjectSchema.add(firstFieldSchema);

        Test_DataField_Schema secondFieldSchema = new Test_DataField_Schema(GeneralTestDataObject_SharedValidator_Second);
        secondFieldSchema.addValidator(testSharedFieldValidator.getSecondFilter());
        dataObjectSchema.add(secondFieldSchema);

        dataObjectSchema.finaliseContainer(BlankTestDataObject.class);

        // Create the test instance
        BlankTestDataObject blankTestDataObject = new BlankTestDataObject(dataObjectSchema);
        blankTestDataObject.setAllValues(null
                , GeneralTestDataObject_SharedValidator_First, 1
                , GeneralTestDataObject_SharedValidator_Second, 1);

        // When both are not ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(GeneralTestDataObject_SharedValidator_First)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(GeneralTestDataObject_SharedValidator_Second)).hasValidValue = false;

        // Test that both do not call the validator
        assertTrue(testSharedFieldValidator.getFirstFilter().isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);
        assertTrue(testSharedFieldValidator.getFirstFilter().isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // When only first is ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(GeneralTestDataObject_SharedValidator_First)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(GeneralTestDataObject_SharedValidator_Second)).hasValidValue = false;

        // Test that the fist does not call validate because the second is not ready
        assertTrue(testSharedFieldValidator.getFirstFilter().isValid(null, null, blankTestDataObject));
        assertEquals(0, test_onlyNewSharedValidator.callCount);

        // Test that the second does call validate because the first is ready
        assertFalse(testSharedFieldValidator.getSecondFilter().isValid(null, null, blankTestDataObject));
        assertEquals(1, test_onlyNewSharedValidator.callCount);

        // When only second is ready.....
        ((Test_DataField) blankTestDataObject.<Integer>getField(GeneralTestDataObject_SharedValidator_First)).hasValidValue = false;
        ((Test_DataField) blankTestDataObject.<Integer>getField(GeneralTestDataObject_SharedValidator_Second)).hasValidValue = true;

        // Test that the second does not call validate because the first is not ready
        assertTrue(testSharedFieldValidator.getSecondFilter().isValid(null, null, blankTestDataObject));
        assertEquals(1, test_onlyNewSharedValidator.callCount);

        // Test that the first does call validate because the second is ready
        assertFalse(testSharedFieldValidator.getFirstFilter().isValid(null, null, blankTestDataObject));
        assertEquals(2, test_onlyNewSharedValidator.callCount);

        // Check that the validator is called when both are ready
        ((Test_DataField) blankTestDataObject.<Integer>getField(GeneralTestDataObject_SharedValidator_First)).hasValidValue = true;
        ((Test_DataField) blankTestDataObject.<Integer>getField(GeneralTestDataObject_SharedValidator_Second)).hasValidValue = true;
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
        public DataField<Integer> generate(DataObject blackObject) {
            return new Test_DataField(this, blackObject);
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
