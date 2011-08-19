package zipbrowser;

import java.awt.AWTException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import zipbrowser.core.ItemResolver;
import zipbrowser.core.MountPointSettings;
import zipbrowser.server.ServerSettings;
import zipbrowser.server.ZipBrowserServer;
import zipbrowser.ui.ZipBrowserUI;

/**
 * @author Tetsuo
 */
public class ZipBrowser {
    private static ZipBrowser INSTANCE = new ZipBrowser();
    private static final Logger log = Logger.getAnonymousLogger();
    private final MountPointSettings mountPointSettings;
    private final ServerSettings serverSettings;
    private final ItemResolver itemResolver;
    private final ZipBrowserServer server;
    private ZipBrowser() {
        mountPointSettings = new MountPointSettings();
        itemResolver = new ItemResolver(mountPointSettings);
        serverSettings = new ServerSettings();
        server = new ZipBrowserServer(serverSettings, itemResolver);
    }
    public ZipBrowserServer getServer() {
        return server;
    }
    public MountPointSettings getMountPointSettings() {
        return mountPointSettings;
    }
    public ServerSettings getServerSettings() {
        return serverSettings;
    }
    public ItemResolver getItemResolver() {
        return itemResolver;
    }
    public static ZipBrowser get() {
        return INSTANCE;
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            log.log(Level.WARNING, ex.getMessage(), ex);
        }
        try {
            new ZipBrowserUI(ZipBrowser.get()).init();
            ZipBrowser.get().getServer().start();
        } catch (AWTException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            System.exit(1);
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            System.exit(2);
        }
    }
}
