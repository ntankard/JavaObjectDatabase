package com.ntankard.javaObjectDatabase.util;

public class Timer {

    /**
     * The start time recorded when the object was created
     */
    private long start;

    /**
     * A string to put on the start of the message
     */
    private String prefix;

    /**
     * Constructor, start the timer
     */
    public Timer() {
        this("");
    }

    /**
     * Constructor, start the timer
     */
    public Timer(String prefix) {
        this.prefix = prefix;
        this.start = System.currentTimeMillis();
    }

    /**
     * Print the time since the object was constructed or the last time stopPrint was called
     */
    public void stopPrint() {
        stopPrint("");
    }

    /**
     * Print the time since the object was constructed or the last time stopPrint was called
     *
     * @param string A message to add
     */
    public void stopPrint(String string) {
        long end = System.currentTimeMillis();
        long delta = end - start;
        System.out.println(prefix + string + ": " + delta);
        start = System.currentTimeMillis();
    }
}
