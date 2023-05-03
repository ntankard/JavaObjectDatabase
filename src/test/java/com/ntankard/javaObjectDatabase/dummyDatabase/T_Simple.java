package com.ntankard.javaObjectDatabase.dummyDatabase;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

public class T_Simple extends DataObject {

    private static final String T_Simple_Prefix = "T_Simple_";

    public static final String T_Simple_Value = T_Simple_Prefix + "Value";
    public static final String T_Simple_NullableValue = T_Simple_Prefix + "NullableValue";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(T_Simple_Value, Double.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Simple_NullableValue, Double.class, true));
        // Children

        return dataObjectSchema.finaliseContainer(T_Simple.class);
    }

    /**
     * Constructor
     */
    public T_Simple(Database database, Object... args) {
        super(database, args);
    }

    /**
     * Constructor
     */
    public T_Simple(Database database, Double value, Double nullableValue) {
        super(database
                , T_Simple_Value, value
                , T_Simple_NullableValue, nullableValue
        );
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Double getValue() {
        return get(T_Simple_Value);
    }

    public Double getNullableValue() {
        return get(T_Simple_NullableValue);
    }
}
