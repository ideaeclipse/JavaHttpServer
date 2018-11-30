package ideaeclipse.JavaHttpServer;

import ideaeclipse.JavaHttpServer.Listener.ConnectionEvent;
import ideaeclipse.JavaHttpServer.Listener.PageData;
import ideaeclipse.AsyncUtility.Async;
import ideaeclipse.CustomProperties.Properties;
import ideaeclipse.JsonUtilities.Json;
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
    public static Properties properties = new Properties(new String[]{"resourceDirectory", "404FilePath", "WrongParameters"});
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
                    System.out.println(input);
                    if (input != null) {
                        header = new HttpHeader(new StringTokenizer(input));
                        parameters = header.getParameters();
                    }
                    if (header != null && header.getFilePath() != null) {
                        Map<String, String> metaData = getData(in, header.getMethod());
                        String token = metaData.get("token");
                        String data = metaData.get("data");
                        Optional<List> pageCall = manager.callEventByAnnotationValue(new ConnectionEvent(token != null ? token : null, data, parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, header.getMethod(), header.getFilePath()));
                        if (pageCall.isPresent()) {
                            Boolean temp = Boolean.parseBoolean(String.valueOf(pageCall.get().get(0)));
                            if (temp) {
                                return Optional.empty();
                            }
                        }
                        if (data == null)
                            manager.callEventByAnnotationValue(new ConnectionEvent(token != null ? token : null, data, parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, "GET", properties.getProperty("404FilePath")));
                        else {
                            Json invalid = new Json();
                            invalid.put("Missing Parameter", pageCall.get().get(0));
                            manager.callEventByAnnotationValue(new ConnectionEvent(token != null ? token : null, invalid.toString(), parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, "GET", properties.getProperty("WrongParameters")));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Optional.empty();
            }, "Client Connection");
        }

        private Map<String, String> getData(final BufferedReader in, final String method) throws IOException {
            String newLine;
            Map<String, String> metaData = new HashMap<>();
            int contentLength = 0;
            while (!(newLine = in.readLine()).equals("")) {
                if (method.equals("POST")) {
                    final String contentHeader = "Content-Length: ";
                    if (newLine.startsWith(contentHeader)) {
                        contentLength = Integer.parseInt(newLine.substring(contentHeader.length()));
                    }
                }
                if (newLine.startsWith("Authorization"))
                    metaData.put("token", newLine.substring(newLine.indexOf(":") + 2));
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < contentLength; i++)
                builder.append((char) in.read());
            metaData.put("data", builder.toString().length() == 0 ? null : builder.toString());

            return metaData;
        }
    }
}
