package zipbrowser.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.prefs.Preferences;
import zipbrowser.ZipBrowser;

public class MountPointSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String KEY_MOUNT_POINTS = "mountPoints";
    private final CopyOnWriteArraySet<MountPointListener> listeners = new CopyOnWriteArraySet<MountPointListener>();
    private final ConcurrentHashMap<String, MountPoint> mountPointsMap = new ConcurrentHashMap<String, MountPoint>();
    public MountPointSettings() {
        load();
    }
    private void load() {
        for (String line : getPreferences().get(KEY_MOUNT_POINTS, "").split("\n")) {
            if (!line.trim().isEmpty()) {
                String[] pair = line.split(" ", 2);
                mountPointsMap.put(pair[0], new MountPoint(pair[0], pair[1]));
            }
        }
    }
    public boolean containsMountPoint(String id) {
        return mountPointsMap.containsKey(id);
    }
    public MountPoint getMountPoint(String id) {
        return mountPointsMap.get(id);
    }
    public Collection<MountPoint> getMountPoints() {
        return new TreeSet<MountPoint>(mountPointsMap.values());
    }
    private void save() {
        StringBuilder sb = new StringBuilder();
        for (MountPoint m : new TreeSet<MountPoint>(mountPointsMap.values())) {
            sb.append(m.getId()).append(" ").append(m.getPath()).append("\n");
        }
        getPreferences().put(KEY_MOUNT_POINTS, sb.toString());
    }
    public void registerMountPoint(MountPoint mountPoint) {
        mountPointsMap.put(mountPoint.getId(), mountPoint);
        for (MountPointListener listener : listeners)
            listener.mountPointRegistered(new MountPointEvent(this, mountPoint));
        save();
    }
    public void unregisterMountPoint(String id) {
        MountPoint mountPoint = mountPointsMap.remove(id);
        for (MountPointListener listener : listeners)
            listener.mountPointUnregistered(new MountPointEvent(this, mountPoint));
        save();
    }
    public void addMountPointListener(MountPointListener listener) {
        listeners.add(listener);
    }
    public void removeMountPointListener(MountPointListener listener) {
        listeners.remove(listener);
    }
    private Preferences getPreferences() {
        return Preferences.userNodeForPackage(ZipBrowser.class);
    }
}
