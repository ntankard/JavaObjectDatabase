package com.ntankard.javaObjectDatabase.dummyDatabase;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

public class T_Special extends DataObject {

    private static final String T_Special_Prefix = "T_Special_";

    public static final String T_Special_Name = T_Special_Prefix + "Name";
    public static final String T_Special_IsDefault = T_Special_Prefix + "IsDefault";
    public static final String T_Special_SpecialType1 = T_Special_Prefix + "SpecialType1";
    public static final String T_Special_SpecialType2 = T_Special_Prefix + "SpecialType2";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(T_Special_Name, String.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Special_IsDefault, Boolean.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Special_SpecialType1, Boolean.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Special_SpecialType2, Boolean.class));
        // Children

        // IsDefault ===================================================================================================
        dataObjectSchema.get(T_Special_IsDefault).setDefaultFlag(true);
        // SpecialType1 ================================================================================================
        dataObjectSchema.get(T_Special_SpecialType1).setSpecialFlag(true);
        // SpecialType2 ================================================================================================
        dataObjectSchema.get(T_Special_SpecialType2).setSpecialFlag(true);
        //==============================================================================================================

        return dataObjectSchema.finaliseContainer(T_Special.class);
    }

    /**
     * Constructor
     */
    public T_Special(Database database, Object... args) {
        super(database, args);
    }

    /**
     * Constructor
     */
    public T_Special(Database database, String name, Boolean isDefault, Boolean specialType1, Boolean specialType2) {
        super(database
                , T_Special_Name, name
                , T_Special_IsDefault, isDefault
                , T_Special_SpecialType1, specialType1
                , T_Special_SpecialType2, specialType2
        );
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public String getName() {
        return get(T_Special_Name);
    }

    public Boolean getIsDefault() {
        return get(T_Special_IsDefault);
    }

    public Boolean getSpecialType1() {
        return get(T_Special_SpecialType1);
    }

    public Boolean getSpecialType2() {
        return get(T_Special_SpecialType2);
    }
}
