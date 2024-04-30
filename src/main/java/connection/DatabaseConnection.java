package connection;

import org.apache.logging.log4j.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/user_demo";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Mysql#1234";
    private static DatabaseConnection instance;

    private static final Logger Log = LogManager.getLogger(DatabaseConnection.class);

    private static Connection connection = null;


    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Log.info("Database connection initialized successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            Log.error("Error initializing database connection: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                Log.info("Database connection closed successfully.");
            } catch (SQLException e) {
                Log.error("Error closing database connection: " + e.getMessage(), e);
            }
        }
    }
}
