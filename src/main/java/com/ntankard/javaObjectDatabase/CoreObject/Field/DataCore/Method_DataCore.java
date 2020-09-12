package com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

public class Method_DataCore<T, ContainerType extends DataObject> extends Calculate_DataCore<T> {

    /**
     * The method to get the value
     */
    private final Getter<T, ContainerType> getter;

    /**
     * Constructor
     */
    public Method_DataCore(Getter<T, ContainerType> getter) {
        this.getter = getter;
    }

    /**
     * {@inheritDoc
     */
    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        return getter.get((ContainerType) getDataField().getContainer());
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Interface Classes ###############################################
    //------------------------------------------------------------------------------------------------------------------

    public interface Getter<T, Source> {
        T get(Source container);
    }
}