package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.testObjects;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.Static_DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;

public class SingleListChain {

    public static class SingleListEnd_TestObject extends DataObject {

        public final static String CoreData = "getCoreData";
        public final static String SCoreData = "getSCoreData";
        public final static String NCoreData = "getNCoreData";

        public static DataObject_Schema getDataObjectSchema() {
            DataObject_Schema dataObjectSchema = DataObject.getDataObjectSchema();

            dataObjectSchema.add(new DataField_Schema<>(CoreData, Integer.class));
            dataObjectSchema.get(CoreData).setManualCanEdit(true);

            dataObjectSchema.add(new DataField_Schema<>(SCoreData, Integer.class));
            dataObjectSchema.get(SCoreData).setDataCore_schema(new Static_DataCore_Schema<>(-1));

            dataObjectSchema.add(new DataField_Schema<>(NCoreData, Integer.class, true));
            dataObjectSchema.get(NCoreData).setManualCanEdit(true);

            return dataObjectSchema.finaliseContainer(SingleChain.SingleEnd_TestObject.class);
        }

        public SingleListEnd_TestObject(Integer coreData, Integer nCoreData, Database database) {
            super(database
                    , CoreData, coreData
                    , NCoreData, nCoreData
            );
        }

        public SingleListEnd_TestObject(Database database, Object... args) {
        super(database, args);
        }
    }

}
