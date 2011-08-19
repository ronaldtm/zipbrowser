package zipbrowser.core;

import java.util.EventObject;

public class MountPointEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private final MountPoint mountPoint;
    public MountPointEvent(MountPointSettings mountPointSettings, MountPoint mountPoint) {
        super(mountPointSettings);
        this.mountPoint = mountPoint;
    }
    public MountPointSettings getMountPointSettings() {
        return (MountPointSettings) getSource();
    }
    public MountPoint getMountPoint() {
        return mountPoint;
    }
}
