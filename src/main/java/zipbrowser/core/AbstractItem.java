package zipbrowser.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class AbstractItem implements Item {
    protected abstract InputStream openInputStream() throws IOException;
    @Override
    public void writeTo(OutputStream output) throws IOException {
        InputStream input = null;
        try {
            input = openInputStream();
            byte[] buffer = new byte[4 * 1024];
            int len;
            while ((len = input.read(buffer)) != -1)
                output.write(buffer, 0, len);
        } finally {
            if (input != null)
                input.close();
        }
    }
    @Override
    public int compareTo(Item item) {
        if (this.equals(item))
            return 0;
        int value = -Boolean.valueOf(this.isDirectory()).compareTo(Boolean.valueOf(item.isDirectory()));
        return (value != 0) ? value : this.name().compareTo(item.name());
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractItem) {
            return (this == obj);
        }
        return super.equals(obj);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
