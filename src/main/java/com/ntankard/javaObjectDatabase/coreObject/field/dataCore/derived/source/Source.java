package com.ntankard.javaObjectDatabase.coreObject.field.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.coreObject.field.DataField;
import com.ntankard.javaObjectDatabase.coreObject.field.dataCore.derived.Derived_DataCore;

public abstract class Source<ResultType> {

    /**
     * A factory to create Source object that can contain state information
     */
    public static abstract class Source_Factory<ResultType, SourceType extends Source<ResultType>> {

        /**
         * Create a stand alone instance of Source that can have state information
         *
         * @param container The DataField this will be attached to
         * @return A stand alone instance of Source that can have state information
         */
        public abstract SourceType createSource(DataField<ResultType> container);
    }

    /**
     * The data core this is attached to
     */
    private Derived_DataCore<ResultType, ?> parent = null;

    /**
     * Had the field been attached?
     */
    private boolean isAttached = false;

    /**
     * Set the data core this is attached to
     *
     * @param parent The data core this is attached to
     */
    public void setParent(Derived_DataCore<ResultType, ?> parent) {
        if (this.parent != null)
            throw new IllegalStateException("The parent can only be set once");

        if (parent == null)
            throw new IllegalStateException("Parent cannot be null");

        if (isAttached)
            throw new IllegalStateException("Cant set the parent after the field is attached (This should never be possible)");

        this.parent = parent;
    }

    /**
     * Get the data core this is attached to
     *
     * @return The data core this is attached to
     */
    protected Derived_DataCore<ResultType, ?> getParent() {
        return parent;
    }

    /**
     * Attach the change listeners
     */
    public void attach() {
        if (parent == null)
            throw new IllegalStateException("Cant attach the source until the parent has been set");

        if (isAttached)
            throw new IllegalStateException("Cant attach the source twice");

        isAttached = true;
        attach_impl();

        doRecalculate();
    }

    /**
     * Detach the change listeners
     */
    public void detach() {
        if (parent == null || !isAttached)
            throw new IllegalStateException("Can't detach the source until it has been attached");

        detach_impl();
        isAttached = false;
        parent = null;
    }

    /**
     * Is the source ready to be derived from?
     *
     * @return True if the source ready to be derived from
     */
    public boolean isReady() {
        if (parent == null)
            throw new IllegalStateException("The parent has not been attached yet");

        if (!isAttached)
            return false;

        return isReady_impl();
    }

    /**
     * Notify the parent to recalculate if other sources are ready
     */
    protected void doRecalculate() {
        if (isReady()) {
            parent.recalculate();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################# Source Implementation ##############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Execute the attach
     */
    protected abstract void attach_impl();

    /**
     * Execute the detach
     */
    protected abstract void detach_impl();

    /**
     * Execute the is ready check. The source state is already checked
     *
     * @return True if the source ready to be derived from
     */
    protected abstract boolean isReady_impl();
}
