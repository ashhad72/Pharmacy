package com.meditrack.pharmacy.singleton;

import java.time.LocalDateTime;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SystemLogger {

    // SINGLE INSTANCE
    private static SystemLogger instance;

    // PRIVATE CONSTRUCTOR
    private SystemLogger() {
    }

    // GLOBAL ACCESS
    public static synchronized SystemLogger getInstance() {

        if (instance == null) {

            instance = new SystemLogger();
        }

        return instance;
    }

    // LOG METHOD
    public void log(String module, String message) {

        String finalMessage =

                "[" + LocalDateTime.now() + "] "

                        + "[" + module + "] "

                        + message;

        System.out.println(finalMessage);

        try (

                PrintWriter writer =

                        new PrintWriter(

                                new FileWriter(
                                        "system_logs.txt",
                                        true
                                )
                        )
        ) {

            writer.println(finalMessage);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}