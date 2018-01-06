package com.qait.automation.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectivity {

	private static String url = "jdbc:mysql://mindtap-qad-0-0-1-qa2-001-us-east-1-mtqapab1.c0ejdcmkjvh6.us-east-1.rds.amazonaws.com:3306/nb";    
    private static String driverName = "com.mysql.jdbc.Driver";   
    private static String username = "manual_user";   
    private static String password = "m4Mw7K42GUet3EV9";
    private static Connection con;
    
    public static Connection getConnection() {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(url, username, password);
                System.out.println("Database connection made successfully");
            } catch (SQLException ex) {
                // log an exception. for example:
                System.out.println("Failed to create the database connection."); 
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found."); 
        }
        return con;
    }
}
