package com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived.source.Source;

public class SelfChild<ResultType, SourceType extends DataObject> extends Source<ResultType> implements DataObject.ChildrenListener<SourceType> {

    /**
     * Constructor
     */
    public SelfChild() {
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean isReady_impl() {
        return true;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void attach_impl() {
        getParent().getDataField().getContainer().addChildrenListener(this);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void detach_impl() {
        getParent().getDataField().getContainer().removeChildrenListener(this);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void childAdded(SourceType dataObject) {
        doRecalculate();
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void childRemoved(SourceType dataObject) {
        doRecalculate();
    }
}
