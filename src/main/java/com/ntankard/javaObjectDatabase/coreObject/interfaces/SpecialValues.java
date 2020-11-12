package com.ntankard.javaObjectDatabase.coreObject.interfaces;

import java.util.List;

public interface SpecialValues {

    /**
     * Is this object the special value?
     *
     * @param key The special object type
     * @return True is this is object the special value?
     */
    Boolean isValue(Integer key);

    /**
     * Get all the special values for this object type
     *
     * @return All the special values for this object type
     */
    List<Integer> toChangeGetKeys();
}
