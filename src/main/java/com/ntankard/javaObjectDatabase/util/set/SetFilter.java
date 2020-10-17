package com.ntankard.javaObjectDatabase.util.set;

public abstract class SetFilter<T> {

    /**
     * The nested filter to run to build up a full filter
     */
    private SetFilter<T> nestFilter;

    /**
     * Constructor
     *
     * @param nestFilter The nested filter to run to build up a full filter (can be null to just run this filter)
     */
    public SetFilter(SetFilter<T> nestFilter) {
        this.nestFilter = nestFilter;
    }

    /**
     * Should this object be included in the set?
     *
     * @param dataObject The object to test
     * @return True if it should be included in the set
     */
    public boolean shouldAdd(T dataObject) {
        if (shouldAdd_Impl(dataObject)) {
            if (nestFilter != null) {
                return nestFilter.shouldAdd(dataObject);
            }
            return true;
        }
        return false;
    }

    /**
     * Should this object be included in the set? This layer of filter only
     *
     * @param dataObject The object to test
     * @return True if this filter thinks it should be included in the set
     */
    protected abstract boolean shouldAdd_Impl(T dataObject);
}
