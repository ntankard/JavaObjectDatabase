package com.ntankard.javaObjectDatabase.CoreObject.Field.Properties;

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

    private Boolean shouldDisplay = true;
    private Integer verbosityLevel = ALWAYS_DISPLAY;
    private Integer order = null;
    private DataType dataType = DataType.AS_CLASS;
    private DataContext dataContext = DataContext.NONE;
    private Integer displayDecimal = 2;
    private Boolean displaySet = true;

    public Boolean getDisplaySet() {
        return displaySet;
    }

    public Boolean getShouldDisplay() {
        return shouldDisplay;
    }

    public Integer getVerbosityLevel() {
        return verbosityLevel;
    }

    public Integer getOrder() {
        return order;
    }

    public DataType getDataType() {
        return dataType;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    public Integer getDisplayDecimal() {
        return displayDecimal;
    }

    public Display_Properties setShouldDisplay(Boolean shouldDisplay) {
        this.shouldDisplay = shouldDisplay;
        return this;
    }

    public Display_Properties setVerbosityLevel(Integer verbosityLevel) {
        this.verbosityLevel = verbosityLevel;
        return this;
    }

    public Display_Properties setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public Display_Properties setDataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    public Display_Properties setDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
        return this;
    }

    public Display_Properties setDisplaySet(Boolean displaySet) {
        this.displaySet = displaySet;
        return this;
    }

    public Display_Properties setDisplayDecimal(Integer displayDecimal) {
        this.displayDecimal = displayDecimal;
        return this;
    }
}
