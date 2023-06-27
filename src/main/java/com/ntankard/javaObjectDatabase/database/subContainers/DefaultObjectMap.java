package com.ntankard.javaObjectDatabase.database.subContainers;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.exception.corrupting.CorruptingException;

public class DefaultObjectMap extends Container<Class, DataObject> {


    /**
     * @inheritDoc
     */
    @Override
    public void add(DataObject dataObject) {
        String defaultKey = dataObject.getSourceSchema().getDefaultFieldKey();
        if (defaultKey != null) {
            if (dataObject.get(defaultKey)) {
                if (container.containsKey(dataObject.getClass())) {
                    throw new CorruptingException(dataObject.getTrackingDatabase(), "Default already set");
                }
                container.put(dataObject.getClass(), dataObject);
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void remove(DataObject dataObject) {
        if (container.containsKey(dataObject.getClass())) {
            if (container.get(dataObject.getClass()).equals(dataObject)) {
                container.remove(dataObject.getClass());
            }
        }
    }

    /**
     * Get the default value that should be used for a specific object type
     *
     * @param aClass The type to get
     * @param <T>    The type, same as aClass
     * @return The default value that should be used for a specific object type
     */
    @SuppressWarnings("unchecked")
    public <T extends DataObject> T getDefault(Class<T> aClass) {
        if (container.containsKey(aClass)) {
            return (T) container.get(aClass);
        }
        return null;
    }
}
