package zipbrowser.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {
    public static String defaultIfNull(String s, String defaultValue) {
        return (s != null) ? s : defaultValue;
    }
    public static String removePrefix(String string, String prefix) {
        return (string.startsWith(prefix)) ? string.substring(prefix.length()) : string;
    }
    public static String removeSuffix(String string, String suffix) {
        return (string.endsWith(suffix)) ? string.substring(0, string.length() - suffix.length()) : string;
    }
    public static String lpad(String s, int length, char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length(); i < length; i++)
            sb.append(c);
        sb.append(s);
        return sb.toString();
    }
    public static String rpad(String s, int length, char c) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = s.length(); i < length; i++)
            sb.append(c);
        return sb.toString();
    }
    public static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++)
            sb.append(s);
        return sb.toString();
    }
    public static String getStackTraceAsString(Exception ex) {
        StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
