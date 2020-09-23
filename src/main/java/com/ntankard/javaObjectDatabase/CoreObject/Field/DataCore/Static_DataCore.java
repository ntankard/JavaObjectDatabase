package com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore;

import com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;

public class Static_DataCore<T> extends DataCore<T> {

    /**
     * The value the fired should always have
     */
    private final T value;

    /**
     * The source of the value the field should always have (only called once during setup
     */
    private final ValueGetter<T> valueGetter;

    /**
     * Constructor
     */
    public Static_DataCore(T value) {
        this.value = value;
        this.valueGetter = null;
    }

    /**
     * Constructor
     */
    public Static_DataCore(ValueGetter<T> valueGetter) {
        this.valueGetter = valueGetter;
        this.value = null;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void startInitialSet() {
        if (valueGetter != null) {
            doSet(valueGetter.get(getDataField()));
        } else {
            doSet(value);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Interface Classes ###############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Interface to extract the static value during setup
     *
     * @param <T>
     */
    public interface ValueGetter<T> {

        /**
         * Get the static value
         *
         * @return The Static value
         */
        T get(DataField<T> dataField);
    }
}
