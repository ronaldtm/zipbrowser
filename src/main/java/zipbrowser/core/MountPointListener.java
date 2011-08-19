package zipbrowser.core;

import java.util.EventListener;

public interface MountPointListener extends EventListener {
    void mountPointRegistered(MountPointEvent evt);
    void mountPointUnregistered(MountPointEvent evt);
}
