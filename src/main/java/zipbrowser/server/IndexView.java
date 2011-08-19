package zipbrowser.server;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import zipbrowser.core.Item;
import zipbrowser.util.Utils;

class IndexView {
    public static void printFiles(PrintWriter writer, List<Item> items, String path) {
        int maxLength = 0;
        for (Item item : items) {
            int length = item.name().length();
            if (item.isDirectory())
                length++;
            maxLength = Math.max(maxLength, length);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        writer.println("<html>");
        writer.println("<head>");
        writer.append("<title>").append("Index of ").append(path).append("</title>").println();
        writer.println("<head>");
        writer.println("<body>");

        writer.append("<h1>").append("Index of ").append(path).append("</h1>").println();

        writer.println("<pre>");
        writer.append("Name");
        for (int i = 2 + maxLength - "Name".length(); i > 0; i--)
            writer.append(" ");
        writer.append("Last Modified     ");
        writer.append("Size").println();

        writer.println("<hr/>");

        if (!"/".equals(path)) {
            writer.println("<a href='..'>../</a>  ");
        }

        for (Item item : items) {
            String name = item.name();
            if (item.isDirectory())
                name += "/";
            writer.append("<a href='").append(name).append("'>");
            writer.append(name);
            writer.append("</a>  ");
            writer.append(Utils.repeat(" ", maxLength - name.length()));
            if (item.lastModified() >= 0)
                writer.append(dateFormat.format(new Date(item.lastModified()))).append("  ");
            else {
                writer.append("                  ");
            }
            writer.append(formatLength(item.length()));
            writer.println();
        }

        writer.println("</pre>");

        writer.println("<hr/>");

        writer.println("</body>");
        writer.println("</html>");
    }
    private static String formatLength(long length) {
        DecimalFormat numberFormat = new DecimalFormat("0.#");
        double div;
        String suffix;
        if (length > 1024 * 1024 * 1024) {
            div = 1024 * 1024 * 1024;
            suffix = "G";
        } else if (length > 1024 * 1024) {
            div = 1024 * 1024;
            suffix = "M";
        } else if (length > 1024) {
            div = 1024;
            suffix = "K";
        } else {
            div = 1;
            suffix = "";
        }
        return numberFormat.format(length / div) + suffix;
    }
}
