package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

import static com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Factory.createDirectDerivedDataCore;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.End_TestObject.Data;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.Step1_TestObject.EndLink;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects.MultiChain.Step2_TestObject.S1Link;

public class MultiChain {

    public static class End_TestObject extends DataObject {

        public final static String Data = "getData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(Data, Integer.class, true));
            dataObjectSchema.get(Data).setManualCanEdit(true);

            return dataObjectSchema.finaliseContainer(End_TestObject.class);
        }

        public End_TestObject(Integer data, Database database) {
            super(database
                    , Data, data
            );
        }

        public End_TestObject(Database database, Object... args) {
            super(database, args);
        }
    }

    public static class Step1_TestObject extends DataObject {

        public final static String EndLink = "getEndLink";
        public final static String EndData = "getEndData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(EndLink, End_TestObject.class, true));
            dataObjectSchema.get(EndLink).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(EndData, Integer.class, true));
            dataObjectSchema.get(EndData).setDataCore_schema(createDirectDerivedDataCore(EndLink, Data));

            return dataObjectSchema.finaliseContainer(Step1_TestObject.class);
        }

        public Step1_TestObject(End_TestObject endLink, Database database) {
            super(database
                    , EndLink, endLink
            );
        }

        public Step1_TestObject(Database database, Object... args) {
            super(database, args);
        }
    }

    public static class Step2_TestObject extends DataObject {

        public final static String S1Link = "getS1Link";
        public final static String S1EndData = "getS1EndData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(S1Link, Step1_TestObject.class, true));
            dataObjectSchema.get(S1Link).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(S1EndData, Integer.class, true));
            dataObjectSchema.get(S1EndData).setDataCore_schema(createDirectDerivedDataCore(S1Link, EndLink, Data));

            return dataObjectSchema.finaliseContainer(Step2_TestObject.class);
        }

        public Step2_TestObject(Step1_TestObject s1Link, Database database) {
            super(database
                    , S1Link, s1Link
            );
        }

        public Step2_TestObject(Database database, Object... args) {
            super(database, args);
        }
    }

    public static class Step3_TestObject extends DataObject {

        public final static String S2Link = "getS2Link";
        public final static String S2S1EndData = "getS2S1EndData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(S2Link, Step2_TestObject.class, true));
            dataObjectSchema.get(S2Link).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(S2S1EndData, Integer.class, true));
            dataObjectSchema.get(S2S1EndData).setDataCore_schema(createDirectDerivedDataCore(S2Link, S1Link, EndLink, Data));

            return dataObjectSchema.finaliseContainer(Step3_TestObject.class);
        }

        public Step3_TestObject(Step2_TestObject s2Link, Database database) {
            super(database
                    , S2Link, s2Link
            );
        }

        public Step3_TestObject(Database database, Object... args) {
            super(database, args);
        }
    }
}
