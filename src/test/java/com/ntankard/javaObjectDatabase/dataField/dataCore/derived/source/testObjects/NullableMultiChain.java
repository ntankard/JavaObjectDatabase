package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

import static com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Factory.createDirectDerivedDataCore;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.End_N_TestObject.NData;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step1_N_TestObject.NEndLink;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.NullableMultiChain.Step2_N_TestObject.NS1Link;

public class NullableMultiChain {

    public static class End_N_TestObject extends DataObject {

        public final static String NData = "getNData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(NData, Integer.class, true));
            dataObjectSchema.get(NData).setManualCanEdit(true);

            return dataObjectSchema.finaliseContainer(End_N_TestObject.class);
        }

        public End_N_TestObject(Integer nData, Database database) {
            super(database
                    , NData, nData
            );
        }

        public End_N_TestObject(Database database, Object... args) {
            super(database, args);
        }
    }

    public static class Step1_N_TestObject extends DataObject {

        public final static String NEndLink = "getNEndLink";
        public final static String NEndNData = "getNEndNData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(NEndLink, End_N_TestObject.class, true));
            dataObjectSchema.get(NEndLink).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(NEndNData, Integer.class, true));
            dataObjectSchema.get(NEndNData).setDataCore_schema(createDirectDerivedDataCore(NEndLink, NData));

            return dataObjectSchema.finaliseContainer(Step1_N_TestObject.class);
        }

        public Step1_N_TestObject(End_N_TestObject nEndLink, Database database) {
            super(database
                    , NEndLink, nEndLink
            );
        }

        public Step1_N_TestObject(Database database, Object... args) {
            super(database, args);
        }
    }

    public static class Step2_N_TestObject extends DataObject {

        public final static String NS1Link = "getNS1Link";
        public final static String NS1NEndNData = "getNS1NEndNData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(NS1Link, Step1_N_TestObject.class, true));
            dataObjectSchema.get(NS1Link).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(NS1NEndNData, Integer.class, true));
            dataObjectSchema.get(NS1NEndNData).setDataCore_schema(createDirectDerivedDataCore(NS1Link, NEndLink, NData));

            return dataObjectSchema.finaliseContainer(Step2_N_TestObject.class);
        }

        public Step2_N_TestObject(Step1_N_TestObject nS1Link, Database database) {
            super(database
                    , NS1Link, nS1Link
            );
        }

        public Step2_N_TestObject(Database database, Object... args) {
            super(database, args);
        }
    }

    public static class Step3_N_TestObject extends DataObject {

        public final static String NS2Link = "getNS2Link";
        public final static String NS2NS1NEndNData = "getNS2NS1NEndNData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(NS2Link, Step2_N_TestObject.class, true));
            dataObjectSchema.get(NS2Link).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(NS2NS1NEndNData, Integer.class, true));
            dataObjectSchema.get(NS2NS1NEndNData).setDataCore_schema(createDirectDerivedDataCore(NS2Link, NS1Link, NEndLink, NData));

            return dataObjectSchema.finaliseContainer(Step3_N_TestObject.class);
        }

        public Step3_N_TestObject(Step2_N_TestObject nS2Link, Database database) {
            super(database
                    , NS2Link, nS2Link
            );
        }

        public Step3_N_TestObject(Database database, Object... args) {
            super(database, args);
        }
    }
}
