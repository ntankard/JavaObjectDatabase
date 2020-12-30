package com.ntankard.javaObjectDatabase.dataField.validator.testObjects;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.validator.Shared_FieldValidator;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

/**
 * A General test object contains a set of fields that can be used for testing
 *
 * @author Nicholas Tankard
 */
public class SharedValidator_TestObject extends DataObject {

    public final static String First = "getFirst";
    public final static String Second = "getSecond";

    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        Shared_FieldValidator<Integer, Integer, ?> sharedFieldValidator = new Shared_FieldValidator<>(
                First,
                Second,
                (firstNewValue, secondNewValue, container) ->
                        !firstNewValue.equals(secondNewValue));

        dataObjectSchema.add(new DataField_Schema<>(First, Integer.class));
        dataObjectSchema.add(new DataField_Schema<>(Second, Integer.class));

        dataObjectSchema.<Integer>get(First).addValidator(sharedFieldValidator.getFirstFilter());
        dataObjectSchema.<Integer>get(First).setManualCanEdit(true);
        dataObjectSchema.<Integer>get(Second).addValidator(sharedFieldValidator.getSecondFilter());
        dataObjectSchema.<Integer>get(Second).setManualCanEdit(true);

        return dataObjectSchema.finaliseContainer(SharedValidator_TestObject.class);
    }

    public SharedValidator_TestObject(Integer sharedValidator_first, Integer sharedValidator_second, Database database) {
        this(database);
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , First, sharedValidator_first
                , Second, sharedValidator_second
        );
    }

    public SharedValidator_TestObject(Database database) {
        super(database);
    }
}
