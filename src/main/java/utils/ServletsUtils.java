package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

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

    public static enum MESSAGE_TYPE {
        STATUS("STATUS"),
        INVALID_FIELDS("INVALID_FIELDS");

        private String name;

        MESSAGE_TYPE(String name) {
            this.name = name;
        }

        public String getValue() {
            return name;
        }
    }

    public static JSONObject buildStatusResponse(final boolean status, final String message) {
        JSONObject rs = new JSONObject();
        final MESSAGE_TYPE msgType = MESSAGE_TYPE.STATUS;
        rs.put("MESSAGE_TYPE", msgType.getValue());
        rs.put("success", status);
        rs.put("msg", message);
        return rs;
    }

    public static JSONObject buildInvalidFieldsResponse(String message, final ArrayList<String> invalidFields) {
        JSONObject rs = new JSONObject();
        final MESSAGE_TYPE msgType = MESSAGE_TYPE.INVALID_FIELDS;
        rs.put("MESSAGE_TYPE", msgType.getValue());
        rs.put("msg", message);
        rs.put("invalidFields", invalidFields);
        return rs;
    }
}
