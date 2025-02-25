package com.listeners;

import utils.DBConnectionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

@WebListener
public class AppListener implements ServletContextListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private static List<String> getColumnNames(final String table) throws SQLException {
        final String q = String.format("""
                SELECT COLUMN_NAME
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_NAME = '%s'
                AND COLUMN_KEY NOT IN ('PRI', 'MUL');
                """, table);
        final List<JSONObject> res = DBConnectionManager.executeQuery(q);
        List<String> tmp = new ArrayList<String>();

        for (final JSONObject jsonObject : res) {
            tmp.add(jsonObject.getString("COLUMN_NAME"));
        }

        return tmp;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        Dotenv dotenv = Dotenv.load();

        dotenv.entries().forEach(entry -> context.setAttribute(entry.getKey(), entry.getValue()));

        LOGGER.info("Variabili di ambiente caricate correttamente");

        try {
            DBConnectionManager.getConnection();
        } catch (Exception e) {
            LOGGER.error("Errore durante apertura della connessione", e);
        }

        try {
            final List<String> utenti = getColumnNames("utenti");
            final List<String> cred = getColumnNames("credenziali");

            context.setAttribute("utentiColumns", utenti);
            context.setAttribute("credenzialiColumns", cred);

            LOGGER.info("I nomi delle colonne di ciascuna tabella sono stai caricati correttamente come var globali.");
        } catch (SQLException e) {
            LOGGER.error("Impossibile caricare i nomi delle colonne come var globali", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnectionManager.closeConnection();
        LOGGER.info("Context distrutto.");
    }
}
