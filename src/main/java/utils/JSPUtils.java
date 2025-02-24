package utils;

import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;

public class JSPUtils {
    public static String getFileName(final HttpServletRequest req) {
        final String path = req.getServletPath();
        final String baseFileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        final String capitalized = StringUtils.capitalize(baseFileName);
        return capitalized;
    }
}
