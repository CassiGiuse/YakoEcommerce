package com.listeners;

import utils.DBConnectionManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.cdimascio.dotenv.Dotenv;

@WebListener
public class AppListener implements ServletContextListener {

    private static final Logger LOGGER = LogManager.getLogger();

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
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnectionManager.closeConnection();
        LOGGER.info("Context distrutto.");
    }
}
