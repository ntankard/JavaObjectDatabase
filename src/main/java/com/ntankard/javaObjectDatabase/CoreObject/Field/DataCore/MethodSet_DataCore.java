package com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

public class MethodSet_DataCore<T, ContainerType extends DataObject> extends Method_DataCore<T, ContainerType> {

    /**
     * The method to get the value to set
     */
    private final Setter<T> setter;

    /**
     * Constructor
     */
    public MethodSet_DataCore(Getter<T, ContainerType> getter, Setter<T> setter) {
        super(getter);
        this.setter = setter;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void set(T toSet) {
        setter.set(getDataField().getContainer(), toSet);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean canEdit() {
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Interface Classes ###############################################
    //------------------------------------------------------------------------------------------------------------------

    public interface Setter<T> {
        void set(DataObject container, T toSet);
    }
}
