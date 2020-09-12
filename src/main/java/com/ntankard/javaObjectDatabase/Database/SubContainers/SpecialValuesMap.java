package com.ntankard.javaObjectDatabase.Database.SubContainers;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Interface.SpecialValues;

import java.util.HashMap;
import java.util.Map;

public class SpecialValuesMap extends Container<Class, Map<Integer, DataObject>> {

    /**
     * {@inheritDoc
     */
    @Override
    public void add(DataObject dataObject) {
        if (SpecialValues.class.isAssignableFrom(dataObject.getClass())) {
            if (!container.containsKey(dataObject.getClass())) {
                container.put(dataObject.getClass(), new HashMap<>());
            }
        }

        if (dataObject instanceof SpecialValues) {
            for (Integer key : ((SpecialValues) dataObject).toChangeGetKeys()) {
                if (((SpecialValues) dataObject).isValue(key)) {
                    Map<Integer, DataObject> keyMap = container.get(dataObject.getClass());
                    if (keyMap.containsKey(key)) {
                        throw new RuntimeException("Double add");
                    }
                    keyMap.put(key, dataObject);
                }
            }
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void remove(DataObject dataObject) {
        checkCanDelete(dataObject);

        if (dataObject instanceof SpecialValues) {
            for (Integer key : ((SpecialValues) dataObject).toChangeGetKeys()) {
                if (((SpecialValues) dataObject).isValue(key)) {
                    Map<Integer, DataObject> keyMap = container.get(dataObject.getClass());
                    if (!keyMap.containsKey(key)) {
                        throw new RuntimeException("Removing a value that dose not exist");
                    }
                    keyMap.remove(key);
                }
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
    public <T extends DataObject> T get(Class<T> aClass, Integer key) {
        return (T) container.get(aClass).get(key);
    }
}
