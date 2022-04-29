package com.ntankard.javaObjectDatabase.testUtil.testDatabases;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;

/**
 * A Blank DataObject that can be used for attaching custom test fields
 *
 * @author Nicholas Tankard
 */
public class BlankTestDataObject extends DataObject {
    public BlankTestDataObject(DataObject_Schema dataObjectSchema, Object... args) {
        super(dataObjectSchema, args);
    }
}
