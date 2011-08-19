package zipbrowser.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import zipbrowser.core.ItemResolver;

class ZipBrowserHandler extends AbstractHandler implements ZipBrowserController.RequestListener {
    private final ZipBrowserController engine;
    public ZipBrowserHandler(ItemResolver itemResolver) {
        this.engine = new ZipBrowserController(itemResolver, this);
    }
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
            throws IOException, ServletException {
        this.engine.handleRequest(request, response);
    }
    @Override
    public void onPostRequest(HttpServletRequest request, HttpServletResponse response) {
        Request baseRequest = (request instanceof Request)
                ? (Request) request
                : HttpConnection.getCurrentConnection().getRequest();
        baseRequest.setHandled(true);
    }
}
