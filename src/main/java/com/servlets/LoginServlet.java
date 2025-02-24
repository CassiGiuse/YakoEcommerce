package com.servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import utils.DBConnectionManager;
import utils.ServletsUtils;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static Logger LOGGER = LogManager.getLogger();

    private static boolean userExists(final String username) throws SQLException {
        final String query = "SELECT COUNT(*) AS count FROM credenziali WHERE username = ?";

        try {
            List<JSONObject> result = DBConnectionManager.executeQuery(query, username);
            if (!result.isEmpty()) {
                int count = result.get(0).getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            throw e;
        }

        return false;
    }

    private static void loadDataToHTTPSession(final HttpServletRequest req, final JSONObject json) throws SQLException {
        HttpSession session = req.getSession();

        for (String key : json.keySet()) {
            Object value = json.get(key);
            session.setAttribute(key, value);
        }
    }

    private static JSONObject login(final HttpServletRequest req, final JSONObject userData) {
        LOGGER.info("Login utente ({}) in corso ...", userData.getString("username"));
        JSONObject result = new JSONObject();
        final String query = """
                    SELECT u.*, c.Username
                    FROM utenti AS u
                    INNER JOIN credenziali AS c ON c.UserID = u.ID
                    WHERE c.username = ? AND c.Passwd = ?;
                """;

        final String password = DigestUtils.sha256Hex(userData.getString("passwd"));
        final String username = userData.getString("username");

        try {
            List<JSONObject> users = DBConnectionManager.executeQuery(query, username, password);
            if (!users.isEmpty()) {
                loadDataToHTTPSession(req, users.get(0));
                LOGGER.info("L'utente {} ha appena effettuato il login.", username);
                result.put("status", true);
                result.put("msg", "Utente loggato correttamente");
            } else {
                result.put("error", "Credenziali non valide");
            }
        } catch (SQLException e) {
            result.put("error", "errore durante esecuzione query");
        }

        return result;
    }

    private static JSONObject signup(final HttpServletRequest req, final JSONObject userData) {
        LOGGER.info("Registrazione utente ({}) in corso ...", userData.getString("username"));
        JSONObject result = new JSONObject();

        final String queryUtenti = "INSERT INTO Utenti (Nome, Cognome, Email, Telefono) VALUES (?, ?, ?, ?);";
        final String queryCredenziali = "INSERT INTO Credenziali (Username, Passwd, UserID) VALUES (?, ?, LAST_INSERT_ID());";

        final String nome = userData.getString("nome");
        final String cognome = userData.getString("cognome");
        final String email = userData.getString("email");
        final String telefono = userData.getString("telefono");
        final String username = userData.getString("username");
        final String passwd = DigestUtils.sha256Hex(userData.getString("passwd"));

        try {
            DBConnectionManager.executeUpdate(queryUtenti, nome, cognome, email, telefono);
            DBConnectionManager.executeUpdate(queryCredenziali, username, passwd);
            LOGGER.info("Registrazione utente ({}) completata correttamente.", username);
            result.put("status", true);
            result.put("msg", "Utente registrato correttamente");
        } catch (SQLException e) {
            LOGGER.error("Registrazione utente ({}) fallita.", username, e);
            result.put("error", "errore durante esecuzione query");
        }

        return result;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final JSONObject userData = ServletsUtils.getJsonFromRequest(request);

        final String username = userData.getString("username");
        boolean userExists;
        try {
            userExists = userExists(username);
        } catch (SQLException e) {
            LOGGER.error("Errore durante esecuzione query", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errore", "errore durante esecuzione query");
            ServletsUtils.sendJSONResponse(response, errorResponse);
            return;
        }

        final JSONObject res = userExists ? login(request, userData) : signup(request, userData);
        ServletsUtils.sendJSONResponse(response, res);
    }
}
