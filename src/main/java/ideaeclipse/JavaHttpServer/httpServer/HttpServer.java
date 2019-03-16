package ideaeclipse.JavaHttpServer.httpServer;

import ideaeclipse.reflectionListener.ListenerManager;
import ideaeclipse.reflectionListener.parents.Listener;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class HttpServer {
    private final ListenerManager listenerManager = new ListenerManager();

    public HttpServer(final List<Listener> listeners, final int port, final ExecutorService service, final File privateDir, final File publicDir) throws IOException {
        for (Listener listener : listeners) {
            this.listenerManager.registerListener(listener);
        }
        LoadWorkDirData data = new LoadWorkDirData(privateDir);
        LoadWorkDirData data1 = new LoadWorkDirData(publicDir);
        ServerSocket serverSocket = new ServerSocket(port);
        while (!service.isShutdown()) {
            service.submit(new InputHandler(listenerManager, serverSocket.accept(), data.getData(), data1.getData()));
        }
    }
}
