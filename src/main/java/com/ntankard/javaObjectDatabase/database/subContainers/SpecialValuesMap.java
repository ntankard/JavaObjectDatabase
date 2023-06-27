package com.ntankard.javaObjectDatabase.database.subContainers;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.exception.corrupting.CorruptingException;

import java.util.HashMap;
import java.util.Map;

public class SpecialValuesMap extends Container<Class, Map<String, DataObject>> {

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    @Override
    public void add(DataObject dataObject) {
        if (!dataObject.getSourceSchema().getSpecialFlagKeys().isEmpty()) {
            if (!container.containsKey(dataObject.getClass())) {
                container.put(dataObject.getClass(), new HashMap<>());
            }

            for (String key : dataObject.getSourceSchema().getSpecialFlagKeys()) {
                if (dataObject.<Boolean>get(key)) {
                    Map<String, DataObject> keyMap = container.get(dataObject.getClass());
                    if (keyMap.containsKey(key)) {
                        throw new CorruptingException(dataObject.getTrackingDatabase(), "Multiple objects with special values found");
                    }
                    keyMap.put(key, dataObject);
                }
            }
        }
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    @Override
    public void remove(DataObject dataObject) {
        for (String key : dataObject.getSourceSchema().getSpecialFlagKeys()) {
            if (dataObject.<Boolean>get(key)) {
                Map<String, DataObject> keyMap = container.get(dataObject.getClass());
                if (!keyMap.containsKey(key)) {
                    throw new CorruptingException(dataObject.getTrackingDatabase(), "Removing a value that dose not exist");
                }
                keyMap.remove(key);
            }
        }
    }

    /**
     * Get the object that is the special value
     *
     * @param aClass The type of object to search
     * @param key    The key to search
     * @return The value of type aClass that is the special value for the key
     */
    @SuppressWarnings("unchecked")
    public <T extends DataObject> T get(Class<T> aClass, String key) {
        if (!container.containsKey(aClass)) {
            return null;
        }
        return (T) container.get(aClass).get(key);
    }
}
