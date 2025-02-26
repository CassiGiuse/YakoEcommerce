package utils;

import static utils.DBConnectionManager.executeQuery;
import static utils.DBConnectionManager.fieldExists;
import static utils.ServletsUtils.buildInvalidFieldsResponse;
import static utils.ServletsUtils.buildStatusResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class UserAuth {

  private static Logger LOGGER = LogManager.getLogger();

  private static String SQL_LOGGER_ERR_MESSAGE = "Errore SQL, impossibile effettuare {}. Utente: {}";

  private static void loadDataToHTTPSession(final HttpServletRequest req, final JSONObject json) throws SQLException {
    HttpSession session = req.getSession();

    for (String key : json.keySet()) {
      Object value = json.get(key);
      session.setAttribute(key, value);
    }
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

  public static JSONObject signup(final HttpServletRequest req, final JSONObject userData) {
    String logMessage;
    final String currentOP = "SIGN UP";
    final String username = userData.getString("username");

    LOGGER.info("Tentattivo di registrazione utente ({}) in corso ...", username);
    JSONObject result = new JSONObject();

    final String queryUtenti = "INSERT INTO Utenti (Nome, Cognome, Email, Telefono) VALUES (?, ?, ?, ?);";
    final String queryCredenziali = "INSERT INTO Credenziali (Username, Passwd, UserID) VALUES (?, ?, LAST_INSERT_ID());";

    final ServletContext cxt = req.getServletContext();
    ArrayList<String> invalidFields = new ArrayList<>();

    try {
      if (isFieldInvalid("credenziali", "username", username)) {
        LOGGER.warn("Utente {} esistente, registrazione fallita.", userData.getString(queryCredenziali));
        return buildStatusResponse(false, "Utente esistente, riprovare!");
      }
    } catch (SQLException e) {
      LOGGER.error(SQL_LOGGER_ERR_MESSAGE, currentOP, username, e);
      return buildStatusResponse(false, "Errore. Registrazione fallita!");
    }

    for (final String tableName : new String[] { "utenti", "credenziali" }) {
      List<String> columnNames = getColumnNamesFromContext(cxt, tableName);

      for (final String cName : columnNames) {
        if (cName.equalsIgnoreCase("passwd"))
          continue;

        String userInfo = getUserInfo(userData, cName);
        if (userInfo == null) {
          logMessage = String.format("Campo %s non fornito durante la registrazione.", cName);
          LOGGER.warn(logMessage);
          return buildStatusResponse(false, logMessage);
        }

        if (userInfo.isBlank()) {
          logMessage = String.format("È necessario specificare il campo %s!", cName);
          LOGGER.warn(logMessage);
          return buildStatusResponse(false, logMessage);
        }

        try {
          if (isFieldInvalid(tableName, cName.toLowerCase(), userInfo)) {
            invalidFields.add(cName);
          }
        } catch (SQLException e) {
          LOGGER.error(SQL_LOGGER_ERR_MESSAGE, currentOP, username, e);
          return buildStatusResponse(false, "Registrazione fallita!");
        }
      }
    }

    if (!invalidFields.isEmpty()) {
      logMessage = "Impossibile effettuare registrazione, alcuni campi non sono validi";
      LOGGER.warn(logMessage);
      return buildInvalidFieldsResponse(logMessage, invalidFields);
    }

    final String nome = userData.getString("nome");
    final String cognome = userData.getString("cognome");
    final String email = userData.getString("email");
    final String telefono = userData.getString("telefono");
    final String passwd = DigestUtils.sha256Hex(userData.getString("passwd"));

    try {
      DBConnectionManager.executeUpdate(queryUtenti, nome, cognome, email, telefono);
      DBConnectionManager.executeUpdate(queryCredenziali, username, passwd);
      LOGGER.info("Registrazione utente ({}) completata correttamente.", username);
      result.put("status", true);
      result.put("msg", "Utente registrato correttamente");
    } catch (SQLException e) {
      LOGGER.error(SQL_LOGGER_ERR_MESSAGE, currentOP, username, e);
      result.put("error", "errore durante esecuzione query");
    }

    return result;
  }

  public static JSONObject login(final HttpServletRequest req, final JSONObject userData) {
    final String username = userData.getString("username");
    final String genericErrorMessage = "Impossibile effettuare il login.";
    final String currentOP = "LOGIN";
    LOGGER.info("Login utente ({}) in corso ...", username);
    final String query = """
            SELECT u.*, c.Username
            FROM utenti AS u
            INNER JOIN credenziali AS c ON c.UserID = u.ID
            WHERE c.username = ? AND c.Passwd = ?;
        """;

    final String password = DigestUtils.sha256Hex(userData.getString("passwd"));

    try {
      if (!isFieldInvalid("credenziali", "username", username)) {
        LOGGER.warn("Utente {} inesistente.", username);
        return buildStatusResponse(false, "Utente inesistente, si prega di registrarsi!");
      }
    } catch (SQLException e) {
      LOGGER.error(SQL_LOGGER_ERR_MESSAGE, currentOP, username, e);
      return buildStatusResponse(false, genericErrorMessage);
    }

    try {
      JSONObject result = new JSONObject();
      List<JSONObject> users = executeQuery(query, username, password);

      if (!users.isEmpty()) {
        loadDataToHTTPSession(req, users.get(0));
        LOGGER.info("L'utente {} ha appena effettuato il login.", username);
        result.put("status", true);
        result.put("msg", "Utente loggato correttamente");
      } else {
        LOGGER.warn("L'utente {} ha appena tentato di effettuare il login fornendo credenziali errate", username);
        result.put("error", "Credenziali non valide");
      }

      return result;
    } catch (SQLException e) {
      LOGGER.error(SQL_LOGGER_ERR_MESSAGE, currentOP, username, e);
      return buildStatusResponse(false, genericErrorMessage);
    }
  }

}
