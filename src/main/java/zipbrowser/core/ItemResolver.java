package zipbrowser.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import zipbrowser.util.Utils;

public class ItemResolver implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Pattern KEY_REGEX = Pattern.compile("\\/([^\\/]*)(?:\\/(.*))?");
    private final MountPointSettings mountPointSettings;
    public ItemResolver(MountPointSettings mountPointSettings) {
        this.mountPointSettings = mountPointSettings;
    }
    public Item resolveItem(String path) throws IOException {
        Item item = null;
        Matcher matcher = KEY_REGEX.matcher(path);
        if (matcher.find()) {
            String key = matcher.group(1);
            String relativePath = Utils.defaultIfNull(matcher.group(2), "");
            item = doResolveItem(key, relativePath);
            if (item != null) {
                return item;
            }
        }
        throw new FileNotFoundException("Resource for " + path + " could not be found");
    }
    private Item doResolveItem(String key, String path) throws ZipException, IOException {
        if (key == null || key.isEmpty())
            return new MountPointListItem(this, mountPointSettings);

        MountPoint mountPoint = mountPointSettings.getMountPoint(key);
        if (mountPoint == null)
            return null;

        File root = new File(mountPoint.getPath());
        if (!root.exists())
            return null;

        File baseFile = findBaseFile(root, path);
        if (baseFile == null || !baseFile.exists())
            return null;

        String internalPath = Utils.removePrefix(baseFile.getAbsolutePath(), root.getAbsolutePath());
        internalPath = internalPath.replaceAll("\\\\", "/");
        internalPath = Utils.removePrefix(internalPath, "/");
        internalPath = Utils.removePrefix(path, internalPath);
        FileItem fileItem = new FileItem(baseFile);
        if (fileItem.isZip()) {
            return new ZipEntryItem(new ZipFile(baseFile), internalPath, "".equals(internalPath));
        } else {
            return fileItem;
        }
    }
    private File findBaseFile(File root, String path) {
        path = Utils.removeSuffix(path, "/");
        File baseFile = (path.isEmpty()) ? root : new File(root, path);
        if (baseFile.exists()) {
            return baseFile;
        } else {
            return findBaseFile(root, path.substring(0, path.lastIndexOf('/') + 1));
        }
    }
}
