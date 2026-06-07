package com.meditrack.pharmacy.service;

import com.meditrack.pharmacy.singleton.DatabaseConnectionManager;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class DatabaseHealthService {

    public void checkDatabaseConnection() {

        try {

            Connection connection =
                    DatabaseConnectionManager
                            .getInstance()
                            .getConnection();

            if (connection != null) {

                System.out.println(
                        "Database singleton working correctly."
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}