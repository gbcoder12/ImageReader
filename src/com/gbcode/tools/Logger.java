package com.gbcode.tools;

/**
 * Tool used to log information to the console.
 */
public class Logger {

    /**
     * Logs basic information to the console.
     * @param msg Message to log.
     */
    public static void info(String msg) {
        System.out.println("[INFO]: "+msg);
    }

    /**
     * Logs warnings to the console.
     * @param msg Message to log.
     */
    public static void warn(String msg) {
        System.out.println("[WARNING}; "+msg);
    }

    /**
     * Logs errors to the console.
     * @param msg Message to log.
     */
    public static void err(String msg) {
        System.out.println("[ERROR]: "+msg);
    }
}
