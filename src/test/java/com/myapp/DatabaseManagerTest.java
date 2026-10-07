package com.myapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled; // Add this import
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DatabaseManagerTest {

    @Test
    @Disabled("Skipping until local MySQL is installed and running") // Add this line
    void canConnectToDatabase() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        
        assertNotNull(conn, "Connection should not be null");
        assertFalse(conn.isClosed(), "Connection should be open");
        
        conn.close();
    }
}