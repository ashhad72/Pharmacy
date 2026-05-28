package com.sda.pharmacy.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {

    // SINGLE INSTANCE
    private static DatabaseConnectionManager instance;

    // SHARED CONNECTION
    private Connection connection;

    // DATABASE CONFIGURATION
    private final String URL =
            "jdbc:mysql://localhost:3306/pharmacy_db";

    private final String USERNAME = "root";

    private final String PASSWORD = "your_password";

    // PRIVATE CONSTRUCTOR
    private DatabaseConnectionManager() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(
                    URL,
                    USERNAME,
                    PASSWORD
            );

            System.out.println(
                    "Singleton database connection established."
            );

        } catch (ClassNotFoundException | SQLException e) {

            e.printStackTrace();
        }
    }

    // GLOBAL ACCESS METHOD
    public static synchronized DatabaseConnectionManager getInstance() {

        if (instance == null) {

            instance = new DatabaseConnectionManager();
        }

        return instance;
    }

    // RETURN SHARED CONNECTION
    public Connection getConnection() {

        return connection;
    }
}