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
    private final EventManager manager;
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
        manager = new EventManager();
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
            Async.queue(x -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                    PrintWriter out = new PrintWriter(connect.getOutputStream(), true);

                    HttpHeader header = null;
                    Parameters parameters = null;
                    String input = in.readLine();
                    if (input != null) {
                        header = new HttpHeader(new StringTokenizer(input));
                        parameters = header.getParameters();
                    }
                    if (header != null && header.getFilePath() != null) {
                        String token = getToken(in);
                        System.out.println("Directory called: " + header.getFilePath());
                        Optional<List> pageCall = manager.callEventByAnnotationValue(new ConnectionEvent(token, parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, header.getMethod(), header.getFilePath()));
                        if (pageCall.isPresent()) {
                            Boolean temp = (Boolean) pageCall.get().get(0);
                            if (temp) {
                                return Optional.empty();
                            }
                        }
                        manager.callEventByAnnotationValue(new ConnectionEvent(token, parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, header.getMethod(), properties.getProperty("404FilePath")));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Optional.empty();
            }, "Client Connection");
        }

        private String getToken(final BufferedReader in) throws IOException {
            String newLine;
            String token = null;
            while ((newLine = in.readLine()) != null) {
                if (newLine.isEmpty())
                    break;
                if (newLine.startsWith("Authorization"))
                    token = newLine.substring(newLine.indexOf(":") + 2);
            }
            return token;
        }
    }
}
