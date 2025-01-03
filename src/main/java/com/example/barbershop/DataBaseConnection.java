package com.example.barbershop;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String databaseName = "barbershop";
        String databaseUser = "postgres";
        String databasePassword = "adidas123";
        String url = "jdbc:postgresql://localhost:5432/" + databaseName;

        try {
            Class.forName("org.postgresql.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
        return databaseLink;
    }
}

