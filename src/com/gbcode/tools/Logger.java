package com.gbcode.tools;

public class Logger {

    public static void info(String msg) {
        System.out.println("[INFO]: "+msg);
    }

    public static void warn(String msg) {
        System.out.println("[WARNING}; "+msg);
    }

    public static void err(String msg) {
        System.out.println("[ERROR]: "+msg);
    }
}
