package com.ntankard.javaObjectDatabase.dummyDatabase;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

import static com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Factory.*;
import static com.ntankard.javaObjectDatabase.dummyDatabase.T_Simple.T_Simple_NullableValue;
import static com.ntankard.javaObjectDatabase.dummyDatabase.T_Simple.T_Simple_Value;
import static com.ntankard.javaObjectDatabase.dummyDatabase.T_Special.T_Special_SpecialType1;

public class T_Base extends DataObject {

    private static final String T_Base_Prefix = "T_Base_";

    public static final String T_Base_SimpleValue = T_Base_Prefix + "SimpleValue";
    public static final String T_Base_SimpleNullValue = T_Base_Prefix + "SimpleNullValue";
    public static final String T_Base_SimpleSettableValue = T_Base_Prefix + "SimpleSettableValue";
    public static final String T_Base_SimpleSettableNullValue = T_Base_Prefix + "SimpleSettableNullValue";
    public static final String T_Base_StaticValue = T_Base_Prefix + "StaticValue";
    public static final String T_Base_StaticGetter = T_Base_Prefix + "StaticGetter";
    public static final String T_Base_StaticNullValue = T_Base_Prefix + "StaticNullValue";
    public static final String T_Base_StaticNullGetter = T_Base_Prefix + "StaticNullGetter";
    public static final String T_Base_DirectDerivedLocal = T_Base_Prefix + "DirectDerivedLocal";
    public static final String T_Base_DirectDerivedLocalNullable = T_Base_Prefix + "DirectDerivedLocalNullable";
    public static final String T_Base_DirectDerivedLocalNullableDefault = T_Base_Prefix + "DirectDerivedLocalNullableDefault";
    public static final String T_Base_T_SpecialNullable = T_Base_Prefix + "T_SpecialNullable";
    public static final String T_Base_DirectDerivedLocalNullableDefaultGetter = T_Base_Prefix + "DirectDerivedLocalNullableDefaultGetter";
    public static final String T_Base_Simple = T_Base_Prefix + "Simple";
    public static final String T_Base_SimpleNullable = T_Base_Prefix + "SimpleNullable";
    public static final String T_Base_DirectDerivedExternal = T_Base_Prefix + "DirectDerivedExternal";
    public static final String T_Base_DirectDerivedExternalEndNullable = T_Base_Prefix + "DirectDerivedExternalEndNullable";
    public static final String T_Base_DirectDerivedExternalStepNullable = T_Base_Prefix + "DirectDerivedExternalStepNullable";
    public static final String T_Base_DirectDerivedExternalEndNullableDefault = T_Base_Prefix + "DirectDerivedExternalEndNullableDefault";
    public static final String T_Base_DirectDerivedExternalStepNullableDefault = T_Base_Prefix + "DirectDerivedExternalStepNullableDefault";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

        // ID
        dataObjectSchema.add(new DataField_Schema<>(T_Base_SimpleValue, Double.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_SimpleNullValue, Double.class, true));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_SimpleSettableValue, Double.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_SimpleSettableNullValue, Double.class, true));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_StaticValue, String.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_StaticGetter, T_Special.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_StaticNullValue, String.class, true));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_StaticNullGetter, String.class, true));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_DirectDerivedLocal, Double.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_DirectDerivedLocalNullable, Double.class, true));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_DirectDerivedLocalNullableDefault, Double.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_T_SpecialNullable, T_Special.class, true));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_DirectDerivedLocalNullableDefaultGetter, T_Special.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_Simple, T_Simple.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_SimpleNullable, T_Simple.class, true));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_DirectDerivedExternal, Double.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_DirectDerivedExternalEndNullable, Double.class, true));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_DirectDerivedExternalStepNullable, Double.class, true));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_DirectDerivedExternalEndNullableDefault, Double.class));
        dataObjectSchema.add(new DataField_Schema<>(T_Base_DirectDerivedExternalStepNullableDefault, Double.class));
        // Children

        // SimpleSettableValue =========================================================================================
        dataObjectSchema.get(T_Base_SimpleSettableValue).setManualCanEdit(true);
        // SimpleSettableNullValue =====================================================================================
        dataObjectSchema.get(T_Base_SimpleSettableNullValue).setManualCanEdit(true);
        // StaticValue =================================================================================================
        dataObjectSchema.get(T_Base_StaticValue).setDataCore_schema(createStaticDataCore("StaticValue"));
        // StaticGetter ================================================================================================
        dataObjectSchema.<T_Special>get(T_Base_StaticGetter).setDataCore_schema(createStaticObjectDataCore(T_Special.class, T_Special_SpecialType1));
        // StaticNullValue =============================================================================================
        dataObjectSchema.get(T_Base_StaticNullValue).setDataCore_schema(createStaticDataCore(null));
        // StaticNullGetter ============================================================================================
        dataObjectSchema.get(T_Base_StaticNullGetter).setDataCore_schema(createStaticDataCore(null));
        // DirectDerivedLocal ==========================================================================================
        dataObjectSchema.<Double>get(T_Base_DirectDerivedLocal).setDataCore_schema(
                createDirectDerivedDataCore(T_Base_SimpleSettableValue));
        // DirectDerivedLocalNullable ==================================================================================
        dataObjectSchema.<Double>get(T_Base_DirectDerivedLocalNullable).setDataCore_schema(
                createDirectDerivedDataCore(T_Base_SimpleNullValue));
        // DirectDerivedLocalNullableDefault ===========================================================================
        dataObjectSchema.<Double>get(T_Base_DirectDerivedLocalNullableDefault).setDataCore_schema(
                createDirectDerivedDataCore(container -> -1.0,
                        T_Base_SimpleNullValue));
        // DirectDerivedLocalNullableDefaultGetter =====================================================================
        dataObjectSchema.<T_Special>get(T_Base_DirectDerivedLocalNullableDefaultGetter).setDataCore_schema(
                createDirectDerivedDataCore(container -> container.getTrackingDatabase().getDefault(T_Special.class),
                        T_Base_T_SpecialNullable));
        // DirectDerivedExternal =======================================================================================
        dataObjectSchema.<Double>get(T_Base_DirectDerivedExternal).setDataCore_schema(
                createDirectDerivedDataCore(T_Base_Simple, T_Simple_Value));
        // DirectDerivedExternalEndNullable ============================================================================
        dataObjectSchema.<Double>get(T_Base_DirectDerivedExternalEndNullable).setDataCore_schema(
                createDirectDerivedDataCore(T_Base_Simple, T_Simple_NullableValue));
        // DirectDerivedExternalStepNullable ===========================================================================
        dataObjectSchema.<Double>get(T_Base_DirectDerivedExternalStepNullable).setDataCore_schema(
                createDirectDerivedDataCore(T_Base_SimpleNullable, T_Simple_Value));
        // DirectDerivedExternalEndNullableDefault =====================================================================
        dataObjectSchema.<Double>get(T_Base_DirectDerivedExternalEndNullableDefault).setDataCore_schema(
                createDirectDerivedDataCore(container -> -1.0,
                        T_Base_Simple, T_Simple_NullableValue));
        // DirectDerivedExternalStepNullableDefault ====================================================================
        dataObjectSchema.<Double>get(T_Base_DirectDerivedExternalStepNullableDefault).setDataCore_schema(
                createDirectDerivedDataCore(container -> -1.0,
                        T_Base_SimpleNullable, T_Simple_Value));
        //==============================================================================================================

        return dataObjectSchema.finaliseContainer(T_Base.class);
    }

    /**
     * Constructor
     */
    public T_Base(Database database, Object... args) {
        super(database, args);
    }

    /**
     * Constructor
     */
    public T_Base(Database database, Double simpleValue, Double simpleNullValue, Double simpleSettableValue, Double simpleSettableNullValue, T_Special t_SpecialNullable, T_Simple simple, T_Simple simpleNullable) {
        super(database
                , T_Base_SimpleValue, simpleValue
                , T_Base_SimpleNullValue, simpleNullValue
                , T_Base_SimpleSettableValue, simpleSettableValue
                , T_Base_SimpleSettableNullValue, simpleSettableNullValue
                , T_Base_T_SpecialNullable, t_SpecialNullable
                , T_Base_Simple, simple
                , T_Base_SimpleNullable, simpleNullable
        );
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Double getSimpleValue() {
        return get(T_Base_SimpleValue);
    }

    public Double getSimpleNullValue() {
        return get(T_Base_SimpleNullValue);
    }

    public Double getSimpleSettableValue() {
        return get(T_Base_SimpleSettableValue);
    }

    public Double getSimpleSettableNullValue() {
        return get(T_Base_SimpleSettableNullValue);
    }

    public String getStaticValue() {
        return get(T_Base_StaticValue);
    }

    public T_Special getStaticGetter() {
        return get(T_Base_StaticGetter);
    }

    public String getStaticNullValue() {
        return get(T_Base_StaticNullValue);
    }

    public String getStaticNullGetter() {
        return get(T_Base_StaticNullGetter);
    }

    public Double getDirectDerivedLocal() {
        return get(T_Base_DirectDerivedLocal);
    }

    public Double getDirectDerivedLocalNullable() {
        return get(T_Base_DirectDerivedLocalNullable);
    }

    public Double getDirectDerivedLocalNullableDefault() {
        return get(T_Base_DirectDerivedLocalNullableDefault);
    }

    public T_Special getT_SpecialNullable() {
        return get(T_Base_T_SpecialNullable);
    }

    public T_Special getDirectDerivedLocalNullableDefaultGetter() {
        return get(T_Base_DirectDerivedLocalNullableDefaultGetter);
    }

    public T_Simple getSimple() {
        return get(T_Base_Simple);
    }

    public T_Simple getSimpleNullable() {
        return get(T_Base_SimpleNullable);
    }

    public Double getDirectDerivedExternal() {
        return get(T_Base_DirectDerivedExternal);
    }

    public Double getDirectDerivedExternalEndNullable() {
        return get(T_Base_DirectDerivedExternalEndNullable);
    }

    public Double getDirectDerivedExternalStepNullable() {
        return get(T_Base_DirectDerivedExternalStepNullable);
    }

    public Double getDirectDerivedExternalEndNullableDefault() {
        return get(T_Base_DirectDerivedExternalEndNullableDefault);
    }

    public Double getDirectDerivedExternalStepNullableDefault() {
        return get(T_Base_DirectDerivedExternalStepNullableDefault);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public void setSimpleSettableValue(Double simpleSettableValue) {
        set(T_Base_SimpleSettableValue, simpleSettableValue);
    }

    public void setSimpleSettableNullValue(Double simpleSettableNullValue) {
        set(T_Base_SimpleSettableNullValue, simpleSettableNullValue);
    }
}
