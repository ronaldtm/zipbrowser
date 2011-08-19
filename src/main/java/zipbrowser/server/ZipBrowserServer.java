package zipbrowser.server;

import java.net.BindException;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mortbay.component.LifeCycle.Listener;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;

import zipbrowser.core.ItemResolver;

public class ZipBrowserServer {
    private static final Logger log = Logger.getAnonymousLogger();
    private final ItemResolver itemResolver;
    private final ServerSettings serverSettings;
    private final Server server = new Server();
    private final Lock lock = new ReentrantLock();
    private int port = 12345;
    public ZipBrowserServer(ServerSettings serverSettings, ItemResolver itemResolver) {
        this.serverSettings = serverSettings;
        this.itemResolver = itemResolver;
        init();
    }
    private void init() {
        locked(new Callable<Void>() {
            @Override
            public Void call() {
                if (server.isRunning()) {
                    try {
                        server.stop();
                        server.join();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    }
                }
                SocketConnector connector = new SocketConnector();
                connector.setPort(port);
                if (serverSettings.isListenLocalhostOnly()) {
                    connector.setHost("localhost");
                }
                server.setConnectors(new Connector[]{connector});
                server.setHandler(new ZipBrowserHandler(itemResolver));
                return null;
            }
        });
    }
    public void start() throws Exception {
        locked(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                do {
                    try {
                        server.start();
                        break;
                    } catch (BindException ex) {
                        log.log(Level.FINER, ex.getMessage(), ex);
                        port++;
                        init();
                    }
                } while (true);
                return null;
            }
        });
    }
    public void stop() throws Exception {
        locked(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                server.stop();
                server.join();
                return null;
            }
        });
    }
    public void addLifecycleListener(final Listener listener) {
        locked(new Callable<Void>() {
            @Override
            public Void call() {
                server.addLifeCycleListener(listener);
                return null;
            }
        });
    }
    public void removeLifecycleListener(final Listener listener) {
        locked(new Callable<Void>() {
            @Override
            public Void call() {
                server.removeLifeCycleListener(listener);
                return null;
            }
        });
    }
    public int getPort() {
        return port;
    }
    private <V> V locked(Callable<V> callable) {
        lock.lock();
        try {
            try {
                return callable.call();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        } finally {
            lock.unlock();
        }
    }
    public ItemResolver getItemResolver() {
        return itemResolver;
    }
}
