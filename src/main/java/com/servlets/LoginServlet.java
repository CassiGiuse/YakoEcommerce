package com.servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import static utils.ServletsUtils.buildStatusResponse;
import static utils.ServletsUtils.getJsonFromRequest;
import static utils.ServletsUtils.sendJSONResponse;

import static utils.UserAuth.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static Logger LOGGER = LogManager.getLogger();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        final JSONObject userData = getJsonFromRequest(request);

        LOGGER.info("Richiesta POST ricevuta, data: {}.", userData);

        boolean isRegistration = false;
        try {
            isRegistration = userData.getString("isRegistration").equals("on");
        } catch (JSONException e) {
            LOGGER.error("Azione richiesta non riconosciuta", e);
            final JSONObject rs = buildStatusResponse(false, "Azione non riconosciuta.");
            sendJSONResponse(response, rs);
            return;
        }

        LOGGER.info("Azione richiesta: `{}`.", isRegistration ? "SIGN UP" : "LOGIN");

        if (!userData.has("username")) {
            final String logMessage = "Campo utente non trovato nei dati ricevuti";
            LOGGER.error("Campo utente non trovato nei dati ricevuti.", userData);
            final JSONObject rs = buildStatusResponse(false, logMessage);
            sendJSONResponse(response, rs);
            return;
        }

        final JSONObject res = isRegistration ? signup(request, userData) : login(request, userData);
        LOGGER.info("Risposta inviata: {}", res.toString());
        sendJSONResponse(response, res);
    }
}
