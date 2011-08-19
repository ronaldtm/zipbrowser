package zipbrowser.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import zipbrowser.ZipBrowser;

public abstract class PreferencesDialog extends JFrame {
    private static final long serialVersionUID = 1L;

    public PreferencesDialog(final ZipBrowser zipBrowser) {
        setTitle("ZipBrowser Preferences");

        JTabbedPane tabbedPane = new JTabbedPane();

        setLayout(new BorderLayout());
        tabbedPane.addTab("Mount Points", new MountPointSettingsPanel(zipBrowser.getMountPointSettings()) {
            @Override
            void onClose() {
                PreferencesDialog.this.onClose();
            }
        });
        tabbedPane.addTab("Advanced", new AdvancedSettingsPanel(zipBrowser.getServerSettings()) {
            @Override
            void onClose() {
                PreferencesDialog.this.onClose();
            }
        });
        add(tabbedPane, BorderLayout.CENTER);

        setSize(640, 240);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evt) {
                PreferencesDialog.this.onClose();
            }
        });
    }

    protected abstract void onClose();
}
