package com.ntankard.javaObjectDatabase.CoreObject.Interface;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Interface.Ordered;

import java.util.Comparator;

public class Ordered_Comparator implements Comparator<DataObject> {

    /**
     * {@inheritDoc
     */
    @Override
    public int compare(DataObject o1, DataObject o2) {
        Integer o1_order = ((Ordered) o1).getOrder();
        Integer o2_order = ((Ordered) o2).getOrder();
        if (o1_order == null || o2_order == null) {
            return 0;
        }

        if (o1_order.equals(o2_order)) {
            return 0;
        } else if (o1_order > o2_order) {
            return 1;
        }
        return -1;
    }
}
