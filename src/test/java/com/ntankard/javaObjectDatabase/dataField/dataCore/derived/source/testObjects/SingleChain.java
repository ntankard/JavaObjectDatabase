package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.Static_DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

import static com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Factory.createDirectDerivedDataCore;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.SingleChain.SingleEnd_TestObject.CoreData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.SingleChain.SingleEnd_TestObject.NullableCoreData;


public class SingleChain {
    public static class SingleEnd_TestObject extends DataObject {

        public final static String CoreData = "getCoreData";
        public final static String CoreData_Derived1 = "getCoreData_Derived1";
        public final static String CoreData_Derived2 = "getCoreData_Derived2";

        public final static String StaticData_Derived1 = "getStaticData_Derived1";
        public final static String StaticData = "getStaticData";
        public final static String StaticData_Derived2 = "getStaticData_Derived2";
        public final static String StaticData_Derived3 = "getStaticData_Derived3";

        public final static String NullableCoreData = "getNullableCoreData";
        public final static String NullableCoreData_Derived1 = "getNullableCoreData_Derived1";
        public final static String NullableCoreData_Derived2 = "getNullableCoreData_Derived2";

        public final static String NullableStaticData_Derived1 = "getNullableStaticData_Derived1";
        public final static String NullableStaticData = "getNullableStaticData";
        public final static String NullableStaticData_Derived2 = "getNullableStaticData_Derived2";
        public final static String NullableStaticData_Derived3 = "getNullableStaticData_Derived3";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            // CoreData

            dataObjectSchema.add(new DataField_Schema<>(CoreData, Integer.class));
            dataObjectSchema.get(CoreData).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(CoreData_Derived1, Integer.class));
            dataObjectSchema.get(CoreData_Derived1).setDataCore_schema(
                    createDirectDerivedDataCore(CoreData));

            dataObjectSchema.add(new DataField_Schema<>(CoreData_Derived2, Integer.class));
            dataObjectSchema.get(CoreData_Derived2).setDataCore_schema(
                    createDirectDerivedDataCore(CoreData_Derived1));

            // StaticData

            dataObjectSchema.add(new DataField_Schema<>(StaticData_Derived1, Integer.class));
            dataObjectSchema.get(StaticData_Derived1).setDataCore_schema(
                    createDirectDerivedDataCore(StaticData));

            dataObjectSchema.add(new DataField_Schema<>(StaticData, Integer.class));
            dataObjectSchema.get(StaticData).setDataCore_schema(new Static_DataCore_Schema<>(10));

            dataObjectSchema.add(new DataField_Schema<>(StaticData_Derived2, Integer.class));
            dataObjectSchema.get(StaticData_Derived2).setDataCore_schema(
                    createDirectDerivedDataCore(StaticData));

            dataObjectSchema.add(new DataField_Schema<>(StaticData_Derived3, Integer.class));
            dataObjectSchema.get(StaticData_Derived3).setDataCore_schema(
                    createDirectDerivedDataCore(StaticData_Derived2));

            // CoreData that can be set to null

            dataObjectSchema.add(new DataField_Schema<>(NullableCoreData, Integer.class, true));
            dataObjectSchema.get(NullableCoreData).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(NullableCoreData_Derived1, Integer.class, true));
            dataObjectSchema.get(NullableCoreData_Derived1).setDataCore_schema(
                    createDirectDerivedDataCore(NullableCoreData));

            dataObjectSchema.add(new DataField_Schema<>(NullableCoreData_Derived2, Integer.class, true));
            dataObjectSchema.get(NullableCoreData_Derived2).setDataCore_schema(
                    createDirectDerivedDataCore(NullableCoreData_Derived1));

            // Null StaticData

            dataObjectSchema.add(new DataField_Schema<>(NullableStaticData_Derived1, Integer.class, true));
            dataObjectSchema.get(NullableStaticData_Derived1).setDataCore_schema(
                    createDirectDerivedDataCore(NullableStaticData));

            dataObjectSchema.add(new DataField_Schema<>(NullableStaticData, Integer.class, true));
            dataObjectSchema.get(NullableStaticData).setDataCore_schema(new Static_DataCore_Schema<>(null));

            dataObjectSchema.add(new DataField_Schema<>(NullableStaticData_Derived2, Integer.class, true));
            dataObjectSchema.get(NullableStaticData_Derived2).setDataCore_schema(
                    createDirectDerivedDataCore(NullableStaticData));

            dataObjectSchema.add(new DataField_Schema<>(NullableStaticData_Derived3, Integer.class, true));
            dataObjectSchema.get(NullableStaticData_Derived3).setDataCore_schema(
                    createDirectDerivedDataCore(NullableStaticData_Derived2));

            return dataObjectSchema.finaliseContainer(SingleEnd_TestObject.class);
        }

        public SingleEnd_TestObject(Integer coreData, Database database) {
            this(coreData, null, database);
        }

        public SingleEnd_TestObject(Integer coreData, Integer nullCoreData, Database database) {
            this(database);
            setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                    , CoreData, coreData
                    , NullableCoreData, nullCoreData
            );
        }

        public SingleEnd_TestObject(Database database) {
            super(database);
        }
    }

    public static class Step_TestObject extends DataObject {

        public final static String Link1 = "getLink1";
        public final static String NullableLink1 = "getNullableLink1";

        public final static String Link1_CoreData = "getLink1_CoreData";
        public final static String Link1_NullableCoreData = "getLink1_NullableCoreData";

        public final static String NullableLink1_CoreData = "getNullableLink1_CoreData";
        public final static String NullableLink1_NullableCoreData = "getNullableLink1_NullableCoreData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(Link1, SingleEnd_TestObject.class));
            dataObjectSchema.get(Link1).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(NullableLink1, SingleEnd_TestObject.class, true));
            dataObjectSchema.get(NullableLink1).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(Link1_CoreData, Integer.class));
            dataObjectSchema.get(Link1_CoreData).setDataCore_schema(createDirectDerivedDataCore(Link1, CoreData));

            dataObjectSchema.add(new DataField_Schema<>(Link1_NullableCoreData, Integer.class, true));
            dataObjectSchema.get(Link1_NullableCoreData).setDataCore_schema(createDirectDerivedDataCore(Link1, NullableCoreData));

            dataObjectSchema.add(new DataField_Schema<>(NullableLink1_CoreData, Integer.class, true));
            dataObjectSchema.get(NullableLink1_CoreData).setDataCore_schema(createDirectDerivedDataCore(NullableLink1, CoreData));

            dataObjectSchema.add(new DataField_Schema<>(NullableLink1_NullableCoreData, Integer.class, true));
            dataObjectSchema.get(NullableLink1_NullableCoreData).setDataCore_schema(createDirectDerivedDataCore(NullableLink1, NullableCoreData));

            return dataObjectSchema.finaliseContainer(Step_TestObject.class);
        }

        public Step_TestObject(SingleEnd_TestObject link1, Database database) {
            this(link1, null, database);
        }

        public Step_TestObject(SingleEnd_TestObject link1, SingleEnd_TestObject nullableLink1, Database database) {
            this(database);
            setAllValues(DataObject_Id, getTrackingDatabase().getNextId()
                    , Link1, link1
                    , NullableLink1, nullableLink1
            );
        }

        public Step_TestObject(Database database) {
            super(database);
        }
    }
}
