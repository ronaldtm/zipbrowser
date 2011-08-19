package zipbrowser.core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

final class FileItem extends AbstractItem {
    private static final Set<String> ZIP_EXTENSIONS = new HashSet<String>(Arrays.asList("zip", "jar", "war", "ear"));
    private static final FileFilter HIDDEN_FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return !f.isHidden();
        }
    };
    private final File file;
    FileItem(File file) {
        this.file = file;
    }
    @Override
    public String name() {
        return file.getName();
    }
    @Override
    public long length() {
        return file.length();
    }
    @Override
    public long lastModified() {
        return file.lastModified();
    }
    @Override
    public boolean isDirectory() {
        return file.isDirectory() || isZip();
    }
    @Override
    protected InputStream openInputStream() throws IOException {
        return new FileInputStream(file);
    }
    @Override
    public Collection<Item> items() throws IOException {
        if (!isDirectory())
            throw new FileNotFoundException();

        File[] files = file.listFiles(HIDDEN_FILE_FILTER);
        files = (files != null) ? files : new File[0];
        Set<Item> items = new TreeSet<Item>();
        if (files != null) {
            for (File f : files) {
                items.add(new FileItem(f));
            }
        }
        return items;
    }
    @Override
    public int hashCode() {
        return file.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileItem) {
            FileItem z = (FileItem) obj;
            return this.file.equals(z.file);
        }
        return false;
    }
    public boolean isZip() {
        String name = file.getName().toLowerCase(Locale.getDefault());
        int index = name.lastIndexOf('.');
        String extension = (index != -1) ? name.substring(index + 1) : "";
        return (ZIP_EXTENSIONS.contains(extension));
    }
}
