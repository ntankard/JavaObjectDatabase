package com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore;

import com.ntankard.javaObjectDatabase.CoreObject.Field.Listener.FieldChangeListener;

public class Static_DataCore<T> extends DataCore<T> {

    /**
     * The value the fired should always have
     */
    private final T value;

    /**
     * Constructor
     */
    public Static_DataCore(T value) {
        this.value = value;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public T get() {
        return value;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void set(T toSet) {
        throw new UnsupportedOperationException("Trying to set a static value");
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void initialSet(T toSet) {
        throw new UnsupportedOperationException("Trying to set a static value");
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean canEdit() {
        return false;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean canInitialSet() {
        return false;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean doseSupportChangeListeners() {
        return true;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void forceInitialSet() {
        super.forceInitialSet();
        for (FieldChangeListener<T> fieldChangeListener : getDataField().getFieldChangeListeners()) {
            fieldChangeListener.valueChanged(getDataField(), null, get());
        }
    }
}
