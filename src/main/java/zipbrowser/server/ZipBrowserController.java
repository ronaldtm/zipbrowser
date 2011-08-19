package zipbrowser.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.zip.ZipException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import zipbrowser.core.Item;
import zipbrowser.core.ItemResolver;

class ZipBrowserController {
    private final ItemResolver itemResolver;
    private RequestListener listener;
    public ZipBrowserController(ItemResolver itemResolver) {
        this.itemResolver = itemResolver;
    }
    public ZipBrowserController(ItemResolver itemResolver, RequestListener listener) {
        this.itemResolver = itemResolver;
        this.listener = listener;
    }
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {
        try {
            boolean sendStatusOK;
            String path = request.getPathInfo();
            if (path.endsWith("/")) {
                sendStatusOK = requestedIndex(response, path);
            } else {
                sendStatusOK = requestedFile(response, path);
            }
            if (sendStatusOK) {
                response.setStatus(HttpServletResponse.SC_OK);
            }
            if (listener != null)
                listener.onPostRequest(request, response);
        } catch (ZipException ex) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
        } catch (FileNotFoundException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        }
    }
    private boolean requestedFile(HttpServletResponse response, String path) throws IOException {
        Item item = itemResolver.resolveItem(path);
        if (item.isDirectory()) {
            response.sendRedirect(path + "/");
            return false;
        } else {
            response.setContentType(MimeTypes.getContentType(path));
            if (item.length() >= 0 && item.length() <= Integer.MAX_VALUE)
                response.setContentLength((int) item.length());
            response.setDateHeader("Last-Modified", item.lastModified());
            item.writeTo(response.getOutputStream());
        }
        return true;
    }
    private boolean requestedIndex(HttpServletResponse response, String path) throws IOException {
        Item item = itemResolver.resolveItem(path);
        if (item.isDirectory()) {
            List<Item> items = new ArrayList<Item>(item.items());
            Collections.sort(items);
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            IndexView.printFiles(writer, items, path);
        }
        return true;
    }

    public interface RequestListener {
        void onPostRequest(HttpServletRequest request, HttpServletResponse response);
    }
}
