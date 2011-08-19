package zipbrowser.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @author RonaldTetsuo
 */
public class DecoratorItem implements Item {
    private final Item delegate;
    public DecoratorItem(Item delegate) {
        this.delegate = delegate;
    }
    @Override
    public String name() {
        return delegate.name();
    }
    @Override
    public long length() {
        return delegate.length();
    }
    @Override
    public long lastModified() {
        return delegate.lastModified();
    }
    @Override
    public boolean isDirectory() {
        return delegate.isDirectory();
    }
    @Override
    public Collection<Item> items() throws IOException {
        return delegate.items();
    }
    @Override
    public void writeTo(OutputStream output) throws IOException {
        delegate.writeTo(output);
    }
    @Override
    public int compareTo(Item o) {
        return delegate.compareTo(o);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DecoratorItem) {
            return (this == obj);
        }
        return delegate.equals(obj);
    }
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
