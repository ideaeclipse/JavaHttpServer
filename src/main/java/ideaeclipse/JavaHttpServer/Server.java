package ideaeclipse.JavaHttpServer;

import ideaeclipse.JavaHttpServer.Listener.ConnectionEvent;
import ideaeclipse.JavaHttpServer.Listener.DynamicConnectionEvent;
import ideaeclipse.JavaHttpServer.Listener.PageData;
import ideaeclipse.AsyncUtility.Async;
import ideaeclipse.CustomProperties.Properties;
import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.reflectionListener.EventManager;
import ideaeclipse.reflectionListener.Listener;
import ideaeclipse.reflectionListener.callByAnnotation.AnnotationSearch;
import ideaeclipse.reflectionListener.callByAnnotation.Handler;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class starts the web server and handles all incoming connections
 *
 * @author ideaeclipse
 */
public class Server {
    private final EventManager manager;
    private final Listener listener;
    public static Properties properties = new Properties(new String[]{"resourceDirectory", "404FilePath", "WrongParameters"});

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
        ServerSocket socket = new ServerSocket(port);
        manager = new EventManager();
        this.listener = listener;
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
                        String dynamic = isDynamic(header.getFilePath());
                        if (Boolean.parseBoolean(dynamic)) {
                            Optional<List> pageCall = manager.callEventByAnnotationValue(new ConnectionEvent(token, data, parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, header.getMethod(), header.getFilePath()));
                            if (pageCall.isPresent()) {
                                boolean temp = Boolean.parseBoolean(String.valueOf(pageCall.get().get(0)));
                                if (temp) {
                                    return Optional.empty();
                                }
                            }
                            if (data == null)
                                manager.callEventByAnnotationValue(new ConnectionEvent(token, null, parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, "GET", properties.getProperty("404FilePath")));
                            else {
                                Json invalid = new Json();
                                pageCall.ifPresent(list -> invalid.put("Missing Parameter", list.get(0)));
                                manager.callEventByAnnotationValue(new ConnectionEvent(token, invalid.toString(), parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, "GET", properties.getProperty("WrongParameters")));
                            }
                        } else {
                            String dir = dynamic.substring(0, dynamic.indexOf("&"));
                            String dynamicValue = dynamic.substring(dynamic.indexOf("&") + 1);
                            manager.callEventByAnnotationValue(new DynamicConnectionEvent(dynamicValue, token, data, parameters != null ? parameters : new Parameters(), new Writer(out)), new AnnotationSearch(PageData.class, header.getMethod(), dir));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Optional.empty();
            }, "Client Connection");
        }

        /**
         * Parses the request header
         * Currently only takes the Authorization header and any data attached on.
         *
         * @param in reader
         * @param method post/not
         * @return map of token,data
         * @throws IOException if the buffered reader is not initialized correctly
         */
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

        /**
         * Determines whether or not the directory you are navigating to is a dynamic directory
         * i.e if you had a user system and you wanted to return the id of the inputted username
         * the dynamic directory could be /users/:username/id
         * your requested directory could be /users/ideaeclipse/id
         * the return value would be /users/:username/id&ideaeclipse
         * And you can parse that based on the index of the and sign.
         * This dynamic value then gets passed in the {@link DynamicConnectionEvent}
         * you can call it by using the method {@link DynamicConnectionEvent#getDynamicValue()}
         *
         * @param dir requested directory
         * @return dynamic directory&dynamicValue
         */
        private String isDynamic(final String dir) {
            List<Method> filteredMethods = Arrays.stream(listener.getClass().getDeclaredMethods()).filter(o -> o.getParameterTypes()[0].equals(DynamicConnectionEvent.class)).collect(Collectors.toList());
            for (Method m : filteredMethods) {
                List<Annotation> annotations = Arrays.stream(m.getDeclaredAnnotations()).filter(o -> o.annotationType().equals(PageData.class)).collect(Collectors.toList());
                PageData annotation = (PageData) annotations.get(0);
                List<String> split = Arrays.asList(annotation.directory().substring(1).split("/"));
                List<String> dirSplit = Arrays.asList(dir.substring(1).split("/"));
                if (split.size() == dirSplit.size()) {
                    String dynamic = "";
                    for (int i = 0; i < split.size(); i++) {
                        if (split.get(i).contains(":"))
                            dynamic = annotation.directory() + "&" + dirSplit.get(i);
                        else if (!split.get(i).equals(dirSplit.get(i)))
                            return String.valueOf(true);
                    }
                    return dynamic;
                }
            }
            return String.valueOf(true);
        }
    }
}
