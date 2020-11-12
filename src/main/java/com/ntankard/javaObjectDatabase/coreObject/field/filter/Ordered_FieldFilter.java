package com.ntankard.javaObjectDatabase.coreObject.field.filter;

import com.ntankard.javaObjectDatabase.coreObject.interfaces.Ordered;
import com.ntankard.javaObjectDatabase.coreObject.DataObject;

public class Ordered_FieldFilter<T extends Ordered, ContainerType extends DataObject> extends FieldFilter<T, ContainerType> {

    /**
     * The type of order
     */
    public enum OrderSequence {ABOVE, BELOW}

    /**
     * The field to compare too
     */
    private final String toCompare;

    /**
     * The type of order
     */
    private final OrderSequence orderSequence;

    /**
     * Constructor
     */
    public Ordered_FieldFilter(String toCompare, OrderSequence orderSequence) {
        this.toCompare = toCompare;
        this.orderSequence = orderSequence;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean isValid(T newValue, T pastValue, ContainerType container) {
        if (newValue != null && pastValue != null) {
            Integer order1 = newValue.getOrder();
            Integer order2 = pastValue.getOrder();

            if (orderSequence.equals(OrderSequence.BELOW)) {
                return order1 < order2;
            } else if (orderSequence.equals(OrderSequence.ABOVE)) {
                return order1 > order2;
            }
        }
        return true;
    }
}
