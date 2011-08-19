package zipbrowser.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class MountPointListItem extends AbstractItem {
    private final ItemResolver itemResolver;
    private final MountPointSettings mountPointSettings;
    public MountPointListItem(ItemResolver itemResolver, MountPointSettings mountPointSettings) {
        this.itemResolver = itemResolver;
        this.mountPointSettings = mountPointSettings;
    }
    @Override
    public String name() {
        return "";
    }
    @Override
    public long length() {
        return 0;
    }
    @Override
    public long lastModified() {
        return 0;
    }
    @Override
    public boolean isDirectory() {
        return true;
    }
    @Override
    protected InputStream openInputStream() throws IOException {
        throw new IllegalStateException("This item doesn't have a byte stream");
    }
    @Override
    public Collection<Item> items() throws IOException {
        List<Item> items = new ArrayList<Item>();
        for (final MountPoint mountPoint : mountPointSettings.getMountPoints()) {
            Item item = itemResolver.resolveItem("/" + mountPoint.getId() + "/");
            item = new DecoratorItem(item) {
                @Override
                public String name() {
                    return mountPoint.getId();
                }
            };
            items.add(item);
        }
        return items;
    }
    @Override
    public int hashCode() {
        return 1;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MountPointListItem) {
            MountPointListItem m = (MountPointListItem) obj;
            return (this.itemResolver == m.itemResolver) && (this.mountPointSettings == m.mountPointSettings);
        }
        return false;
    }
    public boolean isZip() {
        return false;
    }
}
