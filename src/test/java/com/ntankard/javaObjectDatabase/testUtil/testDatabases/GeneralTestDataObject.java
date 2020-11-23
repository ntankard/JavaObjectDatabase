package com.ntankard.javaObjectDatabase.testUtil.testDatabases;

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
public class GeneralTestDataObject extends DataObject {

    public final static String GeneralTestDataObject_SharedValidator_First = "generalTestDataObject_SharedValidator_First";
    public final static String GeneralTestDataObject_SharedValidator_Second = "generalTestDataObject_SharedValidator_Second";

    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        Shared_FieldValidator<Integer, Integer, ?> sharedFieldValidator = new Shared_FieldValidator<>(
                GeneralTestDataObject_SharedValidator_First,
                GeneralTestDataObject_SharedValidator_Second,
                (firstNewValue, secondNewValue, container) ->
                        !firstNewValue.equals(secondNewValue));

        dataObjectSchema.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_First, Integer.class));
        dataObjectSchema.add(new DataField_Schema<>(GeneralTestDataObject_SharedValidator_Second, Integer.class));

        dataObjectSchema.<Integer>get(GeneralTestDataObject_SharedValidator_First).addValidator(sharedFieldValidator.getFirstFilter());
        dataObjectSchema.<Integer>get(GeneralTestDataObject_SharedValidator_First).setManualCanEdit(true);
        dataObjectSchema.<Integer>get(GeneralTestDataObject_SharedValidator_Second).addValidator(sharedFieldValidator.getSecondFilter());
        dataObjectSchema.<Integer>get(GeneralTestDataObject_SharedValidator_Second).setManualCanEdit(true);

        return dataObjectSchema.finaliseContainer(GeneralTestDataObject.class);
    }

    public GeneralTestDataObject(Integer sharedValidator_first, Integer sharedValidator_second, Database database) {
        this(database);
        setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                , GeneralTestDataObject_SharedValidator_First, sharedValidator_first
                , GeneralTestDataObject_SharedValidator_Second, sharedValidator_second
        );
    }

    public GeneralTestDataObject(Database database) {
        super(database);
    }
}
