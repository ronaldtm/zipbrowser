package zipbrowser.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class MimeTypes {
    private static final Logger log = Logger.getAnonymousLogger();
    private static final Properties mimeTypes = new Properties();
    static {
        InputStream input = null;
        try {
            input = MimeTypes.class.getClassLoader().getResourceAsStream("mime-types.properties");
            mimeTypes.load(input);
        } catch (IOException e) {
            throw new IllegalStateException();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    log.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
    }
    public static String getContentType(String path) {
        int index = path.lastIndexOf('.');
        String extension = (index != -1) ? path.substring(index) : "";
        return (!extension.isEmpty()) ? extension : "application/octet-stream";
    }
}
