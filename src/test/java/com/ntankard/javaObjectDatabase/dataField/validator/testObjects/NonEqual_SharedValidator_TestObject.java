package com.ntankard.javaObjectDatabase.dataField.validator.testObjects;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.validator.shared.NonEqual_Shared_FieldValidator;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

/**
 * A General test object contains a set of fields that can be used for testing
 *
 * @author Nicholas Tankard
 */
public class NonEqual_SharedValidator_TestObject extends DataObject {

    public final static String First = "getFirst";
    public final static String Second = "getSecond";

    public final static String First_NullForbidden = "getFirst_nullForbidden";
    public final static String Second_NullForbidden = "getSecond_nullForbidden";

    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        NonEqual_Shared_FieldValidator sharedFieldValidator = new NonEqual_Shared_FieldValidator(First, Second);
        NonEqual_Shared_FieldValidator sharedFieldValidator_NullForbidden = new NonEqual_Shared_FieldValidator(First_NullForbidden, Second_NullForbidden, false);

        dataObjectSchema.add(new DataField_Schema<>(First, Integer.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Second, Integer.class, true));
        dataObjectSchema.add(new DataField_Schema<>(First_NullForbidden, Integer.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Second_NullForbidden, Integer.class, true));

        dataObjectSchema.get(First).addValidator(sharedFieldValidator.getFirstFilter());
        dataObjectSchema.get(First).setManualCanEdit(true);
        dataObjectSchema.get(Second).addValidator(sharedFieldValidator.getSecondFilter());
        dataObjectSchema.get(Second).setManualCanEdit(true);

        dataObjectSchema.get(First_NullForbidden).addValidator(sharedFieldValidator_NullForbidden.getFirstFilter());
        dataObjectSchema.get(First_NullForbidden).setManualCanEdit(true);
        dataObjectSchema.get(Second_NullForbidden).addValidator(sharedFieldValidator_NullForbidden.getSecondFilter());
        dataObjectSchema.get(Second_NullForbidden).setManualCanEdit(true);

        return dataObjectSchema.finaliseContainer(NonEqual_SharedValidator_TestObject.class);
    }

    public NonEqual_SharedValidator_TestObject(Integer sharedValidator_first, Integer sharedValidator_second, Integer sharedValidator_first_nullForbidden, Integer sharedValidator_second__nullForbidden, Database database) {
        this(database);
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , First, sharedValidator_first
                , Second, sharedValidator_second
                , First_NullForbidden, sharedValidator_first_nullForbidden
                , Second_NullForbidden, sharedValidator_second__nullForbidden
        );
    }

    public NonEqual_SharedValidator_TestObject(Database database) {
        super(database);
    }
}
