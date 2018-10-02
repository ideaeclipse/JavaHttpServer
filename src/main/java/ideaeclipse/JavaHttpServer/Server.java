package ideaeclipse.JavaHttpServer;

import ideaeclipse.JavaHttpServer.Listener.ConnectionEvent;
import ideaeclipse.JavaHttpServer.Listener.PageData;
import ideaeclipse.AsyncUtility.Async;
import ideaeclipse.CustomProperties.Properties;
import ideaeclipse.reflectionListener.AnnotationSearch;
import ideaeclipse.reflectionListener.EventManager;
import ideaeclipse.reflectionListener.Handler;
import ideaeclipse.reflectionListener.Listener;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * This class starts the web server and handles all incoming connections
 *
 * @author ideaeclipse
 */
@SuppressWarnings("ALL")
public class Server {
    private final ServerSocket socket;
    private final EventManager<Annotation> manager;
    public static Properties properties = new Properties(new String[]{"resourceDirectory", "404FilePath"});
    private Run run;

    /**
     * Constructor for the server object
     * Registers eventmanager along with custom handler {@link PageData}
     * Registers listener events
     * Starts threads from new connections
     *
     * @param port     the numerical port you want the server to listen on generally 80,8080
     * @param listener listener object which stores your code that executes apon user directory requests
     * @throws IOException throw if the port/address can't be bound
     */
    public Server(final int port, final Listener listener) throws IOException {
        properties.start();
        socket = new ServerSocket(port);
        manager = new EventManager<>();
        manager.registerHandler(new Handler<>(PageData.class));
        manager.registerListener(listener);
        while (true) {
            Thread thread = new Thread(new Run(socket.accept()));
            thread.start();
        }
    }

    /**
     * New thread started for each connection
     * the connection socket is passed to this object
     */
    private class Run implements Runnable {
        private final Socket connect;

        Run(final Socket socket) {
            this.connect = socket;
        }

        /**
         * everytime a client connects the request is parsed
         * it retrives the method (get,post) the directory and the url parameters (map)
         * the the manager tries to find a listener method that satisfies the requests parameters
         * if nothing is found the client is redirected to your specified 404 page
         *
         * @see Parameters
         * @see HttpHeader
         */
        public void run() {
            Async.queue(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                    PrintWriter out = new PrintWriter(connect.getOutputStream(), true);
                    String input = in.readLine();
                    HttpHeader header = null;
                    Parameters parameters = null;
                    if (input != null) {
                        header = new HttpHeader(new StringTokenizer(input));
                        parameters = header.getParameters();
                    }
                    if (header != null && header.getFilePath() != null) {
                        System.out.println("Directory called: " + header.getFilePath());
                        if (!manager.callEventByAnnotationValue(new ConnectionEvent(parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, header.getMethod(), header.getFilePath()))) {
                            manager.callEventByAnnotationValue(new ConnectionEvent(parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, PageData.Method.GET, properties.getProperty("404FilePath")));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }, "Client Connection");
        }
    }
}
