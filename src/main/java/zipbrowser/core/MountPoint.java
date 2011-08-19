package zipbrowser.core;

import java.io.Serializable;

public final class MountPoint implements Comparable<MountPoint>, Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String path;
    public MountPoint(String id, String path) {
        this.id = id;
        this.path = path;
    }
    public String getId() {
        return id;
    }
    public String getPath() {
        return path;
    }
    @Override
    public int compareTo(MountPoint o) {
        int result = getId().compareTo(o.getId());
        return (result != 0) ? result : getPath().compareTo(o.getPath());
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MountPoint) {
            MountPoint m = (MountPoint) obj;
            return getId().equals(m.getId()) && getPath().equals(m.getPath());
        }
        return false;
    }
    @Override
    public int hashCode() {
        return getId().hashCode() * 794615287 + getPath().hashCode();
    }
    @Override
    public String toString() {
        return getId();
    }
}
