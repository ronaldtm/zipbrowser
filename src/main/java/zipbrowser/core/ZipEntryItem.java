package zipbrowser.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import zipbrowser.util.Utils;

class ZipEntryItem extends AbstractItem {
    private final String path;
    private final ZipFile zip;
    private final boolean forceDirectory;
    ZipEntryItem(ZipFile zip, String path, boolean forceDirectory) {
        this.path = Utils.removePrefix(path, "/");
        this.zip = zip;
        this.forceDirectory = forceDirectory;
    }
    private ZipEntry getEntry() {
        return zip.getEntry(path);
    }
    @Override
    public String name() {
        return path.substring(path.lastIndexOf('/') + 1);
    }
    @Override
    public long length() {
        ZipEntry entry = getEntry();
        return (entry != null) ? entry.getSize() : -1;
    }
    @Override
    public long lastModified() {
        ZipEntry entry = getEntry();
        return (entry != null) ? entry.getTime() : -1;
    }
    @Override
    public boolean isDirectory() {
        ZipEntry entry = getEntry();
        return forceDirectory || path.isEmpty() || ((entry != null) && entry.isDirectory());
    }
    @Override
    protected InputStream openInputStream() throws IOException {
        ZipEntry entry = getEntry();
        return (entry != null) ? zip.getInputStream(entry) : new ByteArrayInputStream(new byte[0]);
    }
    @Override
    public Collection<Item> items() throws IOException {
        Set<Item> items = new TreeSet<Item>();
        for (ZipEntry entry : Collections.list(zip.entries())) {
            boolean containsSlash = false;
            String name = Utils.removePrefix(entry.getName(), "/");
            if (name.startsWith(path) && !name.equals(path)) {
                name = name.substring(path.length());
                if (name.contains("/")) {
                    name = name.substring(0, name.indexOf("/"));
                    containsSlash = true;
                }
                items.add(new ZipEntryItem(zip, path + name, containsSlash));
            }
        }
        return items;
    }
    @Override
    public int hashCode() {
        return zip.hashCode() + 68623147 * path.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ZipEntryItem) {
            ZipEntryItem z = (ZipEntryItem) obj;
            return this.zip.equals(z.zip) && this.path.equals(z.path);
        }
        return false;
    }
}