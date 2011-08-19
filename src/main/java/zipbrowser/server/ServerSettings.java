package zipbrowser.server;

import java.util.prefs.Preferences;
import zipbrowser.ZipBrowser;

/**
 * @author Tetsuo
 */
public class ServerSettings {
    private static final String KEY_LISTEN_LOCALHOST_ONLY = "listenLocalhostOnly";
    public boolean isListenLocalhostOnly() {
        return getPreferences().getBoolean(KEY_LISTEN_LOCALHOST_ONLY, true);
    }
    public void setListenLocalhostOnly(boolean listenLocalhostOnly) {
        getPreferences().putBoolean(KEY_LISTEN_LOCALHOST_ONLY, listenLocalhostOnly);
    }
    private Preferences getPreferences() {
        return Preferences.userNodeForPackage(ZipBrowser.class);
    }
}
