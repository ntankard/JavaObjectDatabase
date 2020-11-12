package com.ntankard.javaObjectDatabase.coreObject.field.properties;

public class Display_Properties {

    public static final int ALWAYS_DISPLAY = 0;
    public static final int INFO_DISPLAY = 1;
    public static final int DEBUG_DISPLAY = 2;
    public static final int TRACE_DISPLAY = 3;

    public enum DataType {
        AS_CLASS,       // DataType based on class
        CURRENCY,       // currency, can be any currency, field must be double otherwise this is ignored
        CURRENCY_AUD,   // AUD currency, field must be double otherwise this is ignored
        CURRENCY_YEN,   // YEN currency, field must be double otherwise this is ignored
    }

    public enum DataContext {
        NONE,           // Data values have no specific value
        ZERO_BELOW_BAD, // Data above zero is normal but anything below 0 is noteworthy
        ZERO_BINARY,    // Data is centered on zero, values above or below are note worthy
        ZERO_SCALE,     // Data is centered on zero, values above or below are note worthy compared to the range of all values
        ZERO_TARGET,    // Values other than 0 are noteworthy
        NOT_FALSE,      // Boolean false values are highlighted
    }

    private boolean isFinished = false;

    private boolean shouldDisplay = true;
    private int verbosityLevel = ALWAYS_DISPLAY;
    private int order = -1;
    private DataType dataType = DataType.AS_CLASS;
    private DataContext dataContext = DataContext.NONE;
    private int displayDecimal = 2;
    private boolean displaySet = true;

    public void finish() {
        isFinished = true;
    }

    public boolean getDisplaySet() {
        return displaySet;
    }

    public boolean getShouldDisplay() {
        return shouldDisplay;
    }

    public int getVerbosityLevel() {
        return verbosityLevel;
    }

    public int getOrder() {
        return order;
    }

    public DataType getDataType() {
        return dataType;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    public int getDisplayDecimal() {
        return displayDecimal;
    }

    public Display_Properties setShouldDisplay(boolean shouldDisplay) {
        if (this.isFinished)
            throw new IllegalStateException("Cant set values once the properties are finalised");
        this.shouldDisplay = shouldDisplay;
        return this;
    }

    public Display_Properties setVerbosityLevel(int verbosityLevel) {
        if (this.isFinished)
            throw new IllegalStateException("Cant set values once the properties are finalised");
        this.verbosityLevel = verbosityLevel;
        return this;
    }

    public Display_Properties setOrder(int order) {
        if (this.isFinished)
            throw new IllegalStateException("Cant set values once the properties are finalised");
        this.order = order;
        return this;
    }

    public Display_Properties setDataType(DataType dataType) {
        if (this.isFinished)
            throw new IllegalStateException("Cant set values once the properties are finalised");
        this.dataType = dataType;
        return this;
    }

    public Display_Properties setDataContext(DataContext dataContext) {
        if (this.isFinished)
            throw new IllegalStateException("Cant set values once the properties are finalised");
        this.dataContext = dataContext;
        return this;
    }

    public Display_Properties setDisplaySet(boolean displaySet) {
        if (this.isFinished)
            throw new IllegalStateException("Cant set values once the properties are finalised");
        this.displaySet = displaySet;
        return this;
    }

    public Display_Properties setDisplayDecimal(int displayDecimal) {
        if (this.isFinished)
            throw new IllegalStateException("Cant set values once the properties are finalised");
        this.displayDecimal = displayDecimal;
        return this;
    }
}
