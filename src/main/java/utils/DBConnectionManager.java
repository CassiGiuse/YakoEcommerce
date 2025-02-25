package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBConnectionManager {
    private static Connection connection = null;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String dbEnvPath = "src/main/resources/db";

    private static final Dotenv dotenv = Dotenv.configure().directory(dbEnvPath).load();

    private static final String DB_NAME = dotenv.get("DATABASE_NAME");
    private static final String DB_USER = dotenv.get("DATABASE_USER");
    private static final String DB_PASSWORD = dotenv.get("DATABASE_PASSWD");

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                final String dbUrl = "jdbc:mysql://localhost:3306/" + DB_NAME + "?serverTimezone=UTC";

                DBConnectionManager.connection = DriverManager.getConnection(dbUrl, DB_USER, DB_PASSWORD);
                LOGGER.info("Connessione al db `{}` aperta", DB_NAME);
                return connection;
            } catch (ClassNotFoundException e) {
                LOGGER.error("Driver JDBC non trovato");
                throw new SQLException("Driver JDBC non trovato", e);
            }
        }

        return DBConnectionManager.connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.error("Errore durante chiusura della connessione al db", e);

        }
    }

    public static List<JSONObject> executeQuery(String sql, Object... parameters) throws SQLException {
        List<JSONObject> results = new ArrayList<>();
        final Connection conn = DBConnectionManager.connection;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }
            try (ResultSet rSet = stmt.executeQuery()) {
                int columnCount = rSet.getMetaData().getColumnCount();
                while (rSet.next()) {
                    JSONObject row = new JSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rSet.getMetaData().getColumnName(i);
                        row.put(columnName, rSet.getObject(i));
                    }
                    results.add(row);
                }
            }
        }
        return results;
    }

    public static int executeUpdate(String sql, Object... parameters) throws SQLException {
        final Connection conn = DBConnectionManager.connection;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }
            return stmt.executeUpdate();
        }
    }

    public static boolean fieldExists(final String tableName, final String field, final String param)
            throws SQLException {
        final String query = "SELECT COUNT(*) AS count FROM " + tableName + " WHERE " + field + " = ?";

        try {
            List<JSONObject> result = DBConnectionManager.executeQuery(query, param);
            if (!result.isEmpty()) {
                int count = result.get(0).getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Errore durante esecuzione {}", query, e);
            throw e;
        }

        return false;
    }

}
