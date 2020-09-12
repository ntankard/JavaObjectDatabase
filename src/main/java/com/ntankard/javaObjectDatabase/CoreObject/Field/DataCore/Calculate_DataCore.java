package com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore;

public abstract class Calculate_DataCore<T> extends DataCore<T> {

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
    public void set(T toSet) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void initialSet(T toSet) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean doseSupportChangeListeners() {
        return false;
    }
}
