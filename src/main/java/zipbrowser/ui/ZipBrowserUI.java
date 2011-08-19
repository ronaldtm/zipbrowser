package zipbrowser.ui;

import zipbrowser.server.ZipBrowserServer;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.mortbay.component.LifeCycle;
import org.mortbay.component.LifeCycle.Listener;

import zipbrowser.ZipBrowser;
import zipbrowser.core.MountPoint;
import zipbrowser.core.MountPointEvent;
import zipbrowser.core.MountPointListener;

public class ZipBrowserUI {
    private static final Logger log = Logger.getAnonymousLogger();
    private static final Image ICON_STOPPED = createIconImage(ZipBrowserUI.class, "/icon-stopped.png");
    private static final Image ICON_RUNNING = createIconImage(ZipBrowserUI.class, "/icon-running.png");
    private static final Image ICON_FAIL = createIconImage(Color.BLACK);
    private final ZipBrowserServer server;
    private final ZipBrowser zipBrowser;
    private TrayIcon trayIcon;
    private final Menu gotoMenu = new Menu("Go to");
    private final PopupMenu popupMenu = new PopupMenu();
    private PreferencesDialog preferencesDialog;

    public ZipBrowserUI(ZipBrowser zipBrowser) {
        this.zipBrowser = zipBrowser;
        this.server = zipBrowser.getServer();
        server.addLifecycleListener(new LifeCycleListener());

        trayIcon = new TrayIcon(ICON_STOPPED, "Starting...", createPopupMenu());
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                openPreferencesDialog();
            }
        });

        preferencesDialog = new PreferencesDialog(zipBrowser) {
        @Override
        protected void onClose() {
            setVisible(false);
        }
    };
    }

    public void init() throws AWTException {
        SystemTray.getSystemTray().add(trayIcon);
    }

    private PopupMenu createPopupMenu() {
        gotoMenu.setShortcut(new MenuShortcut(KeyEvent.VK_G));
        reloadMountPointMenuItems();
        popupMenu.add(gotoMenu);
        zipBrowser.getMountPointSettings().addMountPointListener(new MountPointListenerImpl());

        MenuItem start = new MenuItem("Start");
        start.addActionListener(new StartAction());

        MenuItem stop = new MenuItem("Stop");
        stop.addActionListener(new StopAction());

        Menu serverMenu = new Menu("Server");
        serverMenu.setShortcut(new MenuShortcut(KeyEvent.VK_S));
        serverMenu.add(start);
        serverMenu.add(stop);
        popupMenu.add(serverMenu);
        popupMenu.addSeparator();

        MenuItem preferences = new MenuItem("Preferences");
        preferences.setShortcut(new MenuShortcut(KeyEvent.VK_P));
        preferences.addActionListener(new PreferencesDialogAction());
        popupMenu.add(preferences);
        popupMenu.addSeparator();

        MenuItem close = new MenuItem("Close");
        close.setShortcut(new MenuShortcut(KeyEvent.VK_C));
        close.addActionListener(new FinalizeAction());
        popupMenu.add(close);

        return popupMenu;
    }

    private void reloadMountPointMenuItems() {
        gotoMenu.removeAll();
        for (MountPoint mountPoint : zipBrowser.getMountPointSettings().getMountPoints()) {
            MenuItem mountItem = new MenuItem(mountPoint.getId());
            mountItem.addActionListener(new OpenMountPoint(mountPoint.getId()));
            gotoMenu.add(mountItem);
        }
    }

    private static void handleException(Level level, Exception ex) {
        log.log(level, ex.getMessage(), ex);

        int messageType;
        if (Level.SEVERE.equals(level)) {
            messageType = JOptionPane.ERROR_MESSAGE;
        } else if (Level.WARNING.equals(level)) {
            messageType = JOptionPane.WARNING_MESSAGE;
        } else if (Level.INFO.equals(level)) {
            messageType = JOptionPane.INFORMATION_MESSAGE;
        } else {
            messageType = JOptionPane.PLAIN_MESSAGE;
        }
        JOptionPane.showMessageDialog(new JButton(), ex.getMessage(), level.getLocalizedName(), messageType);
    }

    private static Image createIconImage(Class<?> baseClazz, String resourcePath) {
        try {
            return ImageIO.read(baseClazz.getResource(resourcePath));
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private static Image createIconImage(Color color) {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, 64, 64);
        return image;
    }

    private void openPreferencesDialog() {
        preferencesDialog.setVisible(true);
    }

    // ************************************************************************
    // Commands
    // ************************************************************************
    private final class UpdateCommand implements Runnable {
        private final Image iconImage;

        private UpdateCommand(Image iconImage) {
            this.iconImage = iconImage;
        }

        @Override
        public void run() {
            trayIcon.setImage(iconImage);
            trayIcon.setToolTip(("ZipBrowser - listening port " + server.getPort()));
            reloadMountPointMenuItems();
        }
    }
    // ************************************************************************
    // External components listeners
    // ************************************************************************
    private final class MountPointListenerImpl implements MountPointListener {
        @Override
        public void mountPointUnregistered(MountPointEvent evt) {
            reloadMountPointMenuItems();
        }

        @Override
        public void mountPointRegistered(MountPointEvent evt) {
            reloadMountPointMenuItems();
        }
    }
    private final class LifeCycleListener implements Listener {
        @Override
        public void lifeCycleStopping(LifeCycle lifeCycle) {
            SwingUtilities.invokeLater(new UpdateCommand(ICON_STOPPED));
        }

        @Override
        public void lifeCycleStopped(LifeCycle lifeCycle) {
            SwingUtilities.invokeLater(new UpdateCommand(ICON_STOPPED));
        }

        @Override
        public void lifeCycleStarting(LifeCycle lifeCycle) {
            SwingUtilities.invokeLater(new UpdateCommand(ICON_STOPPED));
        }

        @Override
        public void lifeCycleStarted(LifeCycle lifeCycle) {
            SwingUtilities.invokeLater(new UpdateCommand(ICON_RUNNING));
        }

        @Override
        public void lifeCycleFailure(LifeCycle lifeCycle, Throwable t) {
            SwingUtilities.invokeLater(new UpdateCommand(ICON_FAIL));
        }
    }
    // ************************************************************************
    // UI Actions
    // ************************************************************************
    private final class StartAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                server.start();
            } catch (Exception ex) {
                handleException(Level.SEVERE, ex);
            }
        }
    }
    private final class StopAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                server.stop();
            } catch (Exception ex) {
                handleException(Level.SEVERE, ex);
            }
        }
    }
    private final class PreferencesDialogAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            openPreferencesDialog();
        }
    }
    private final class OpenMountPoint implements ActionListener {
        private final String mountPoint;

        private OpenMountPoint(String mountPoint) {
            this.mountPoint = mountPoint;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:" + server.getPort() + "/" + mountPoint + "/"));
            } catch (IOException ex) {
                handleException(Level.WARNING, ex);
            } catch (URISyntaxException ex) {
                handleException(Level.WARNING, ex);
            }
        }
    }
    private final class FinalizeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                server.stop();
            } catch (Exception ex) {
                handleException(Level.WARNING, ex);
            } finally {
                SystemTray.getSystemTray().remove(trayIcon);
                log.info("Exiting...");
                System.exit(0);
            }
        }
    }
}
