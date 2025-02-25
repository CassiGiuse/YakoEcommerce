package utils;

import java.util.ArrayList;

import org.json.JSONObject;

public class JsonResponder {
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
