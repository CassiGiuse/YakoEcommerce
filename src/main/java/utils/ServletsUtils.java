package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class ServletsUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    public static JSONObject getJsonFromRequest(HttpServletRequest request) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JSONObject inputJson = new JSONObject(sb.toString());

        return inputJson;
    }

    public static void sendJSONResponse(final HttpServletResponse response, final JSONObject data) throws IOException {
        final PrintWriter out = response.getWriter();
        final String jsonString = data.toString();
        out.print(jsonString);
        out.flush();
        LOGGER.info("Dati inviati correttamente: {}", jsonString);
    }
}
