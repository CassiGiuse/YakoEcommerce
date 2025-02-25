package com.servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import utils.DBConnectionManager;

// import utils.DBConnectionManager;

import static utils.DBConnectionManager.*;
import static utils.ServletsUtils.*;
import static utils.JsonResponder.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static Logger LOGGER = LogManager.getLogger();

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
            if (!isFieldInvalid("credenziali", "username", username)) {
                return buildStatusResponse(false, "Utente inesistente, riprovare!");
            }
        } catch (SQLException e) {
            return buildStatusResponse(false, "Registrazione fallita!");
        }

        try {
            List<JSONObject> users = executeQuery(query, username, password);
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

        final ServletContext cxt = req.getServletContext();
        ArrayList<String> invalidFields = new ArrayList<>();

        try {
            if (isFieldInvalid("credenziali", "username", userData.getString("username"))) {
                return buildStatusResponse(false, "Utente esistente, riprovare!");
            }
        } catch (SQLException e) {
            return buildStatusResponse(false, "Registrazione fallita!");
        }

        for (final String tableName : new String[] { "utenti", "credenziali" }) {
            List<String> columnNames = getColumnNamesFromContext(cxt, tableName);

            for (final String cName : columnNames) {
                if (cName.equalsIgnoreCase("passwd"))
                    continue;

                String userInfo = getUserInfo(userData, cName);
                if (userInfo == null) {
                    return buildStatusResponse(false,
                            String.format("Campo %s non fornito durante la registrazione.", cName));
                }

                if (userInfo.isBlank()) {
                    return buildStatusResponse(false, String.format("È necessario specificare il campo %s!", cName));
                }

                try {
                    if (isFieldInvalid(tableName, cName.toLowerCase(), userInfo)) {
                        invalidFields.add(cName);
                    }
                } catch (SQLException e) {
                    return buildStatusResponse(false, "Registrazione fallita!");
                }
            }
        }

        if (!invalidFields.isEmpty()) {
            final String errorMessage = "Impossibile effettuare registrazione, alcuni campi non sono validi";
            return buildInvalidFieldsResponse(errorMessage, invalidFields);
        }

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

    @SuppressWarnings("unchecked")
    private static List<String> getColumnNamesFromContext(ServletContext cxt, String tableName) {
        final String attrName = String.format("%sColumns", tableName);
        return (List<String>) cxt.getAttribute(attrName);
    }

    private static String getUserInfo(JSONObject userData, String cName) {
        try {
            return userData.getString(cName.toLowerCase());
        } catch (JSONException e) {
            return null;
        }
    }

    private static boolean isFieldInvalid(String tableName, String key, String userInfo) throws SQLException {
        return fieldExists(tableName, key, userInfo);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final JSONObject userData = getJsonFromRequest(request);
        System.out.println(userData.toString());
        final boolean isRegistration = userData.getString("isRegistration").equals("on");

        final JSONObject res = isRegistration ? signup(request, userData) : login(request, userData);
        sendJSONResponse(response, res);
    }
}
