package com.ntankard.javaObjectDatabase.coreObject.field.listener;

public abstract class Marked_FieldChangeListener<T> implements FieldChangeListener<T> {

    /**
     * The ID used to find the listener
     */
    private final String id;

    /**
     * Constructor
     */
    public Marked_FieldChangeListener(String id) {
        this.id = id;
    }

    /**
     * Get the ID used to find the listener
     *
     * @return The ID used to find the listener
     */
    public String getId() {
        return id;
    }
}
