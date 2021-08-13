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
    public final static String Third = "getThird";

    public final static String First_NullForbidden = "getFirst_nullForbidden";
    public final static String Second_NullForbidden = "getSecond_nullForbidden";
    public final static String Third_NullForbidden = "getThird_nullForbidden";

    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        NonEqual_Shared_FieldValidator sharedFieldValidator = new NonEqual_Shared_FieldValidator(First, Second, Third);
        NonEqual_Shared_FieldValidator sharedFieldValidator_NullForbidden = new NonEqual_Shared_FieldValidator(false, First_NullForbidden, Second_NullForbidden, Third_NullForbidden);

        dataObjectSchema.add(new DataField_Schema<>(First, Integer.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Second, Integer.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Third, Integer.class, true));
        dataObjectSchema.add(new DataField_Schema<>(First_NullForbidden, Integer.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Second_NullForbidden, Integer.class, true));
        dataObjectSchema.add(new DataField_Schema<>(Third_NullForbidden, Integer.class, true));

        dataObjectSchema.get(First).addValidator(sharedFieldValidator.getValidator(First));
        dataObjectSchema.get(First).setManualCanEdit(true);
        dataObjectSchema.get(Second).addValidator(sharedFieldValidator.getValidator(Second));
        dataObjectSchema.get(Second).setManualCanEdit(true);
        dataObjectSchema.get(Third).addValidator(sharedFieldValidator.getValidator(Third));
        dataObjectSchema.get(Third).setManualCanEdit(true);

        dataObjectSchema.get(First_NullForbidden).addValidator(sharedFieldValidator_NullForbidden.getValidator(First_NullForbidden));
        dataObjectSchema.get(First_NullForbidden).setManualCanEdit(true);
        dataObjectSchema.get(Second_NullForbidden).addValidator(sharedFieldValidator_NullForbidden.getValidator(Second_NullForbidden));
        dataObjectSchema.get(Second_NullForbidden).setManualCanEdit(true);
        dataObjectSchema.get(Third_NullForbidden).addValidator(sharedFieldValidator_NullForbidden.getValidator(Third_NullForbidden));
        dataObjectSchema.get(Third_NullForbidden).setManualCanEdit(true);

        return dataObjectSchema.finaliseContainer(NonEqual_SharedValidator_TestObject.class);
    }

    public NonEqual_SharedValidator_TestObject(Integer sharedValidator_first, Integer sharedValidator_second, Integer sharedValidator_third, Integer sharedValidator_first_nullForbidden, Integer sharedValidator_second_nullForbidden,  Integer sharedValidator_third_nullForbidden, Database database) {
        this(database);
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , First, sharedValidator_first
                , Second, sharedValidator_second
                , Third, sharedValidator_third
                , First_NullForbidden, sharedValidator_first_nullForbidden
                , Second_NullForbidden, sharedValidator_second_nullForbidden
                , Third_NullForbidden, sharedValidator_third_nullForbidden
        );
    }

    public NonEqual_SharedValidator_TestObject(Database database) {
        super(database);
    }
}
