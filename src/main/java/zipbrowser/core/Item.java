package zipbrowser.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public interface Item extends Comparable<Item> {
    String name();
    long length();
    long lastModified();
    boolean isDirectory();
    Collection<Item> items() throws IOException;
    void writeTo(OutputStream output) throws IOException;
}
