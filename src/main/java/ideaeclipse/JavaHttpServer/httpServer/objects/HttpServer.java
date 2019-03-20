package ideaeclipse.JavaHttpServer.httpServer.objects;

import ideaeclipse.JavaHttpServer.httpServer.PageInfo;
import ideaeclipse.JavaHttpServer.httpServer.Util;
import ideaeclipse.JavaHttpServer.httpServer.incomingData.IInitialRequest;
import ideaeclipse.JavaHttpServer.httpServer.incomingData.objects.InitialRequest;
import ideaeclipse.JavaHttpServer.httpServer.responses.IResponse;
import ideaeclipse.JavaHttpServer.httpServer.responses.objects.RequestMethod;
import ideaeclipse.JavaHttpServer.httpServer.responses.objects.Response;
import ideaeclipse.reflectionListener.Exceptions.EventNotFound;
import ideaeclipse.reflectionListener.Executable;
import ideaeclipse.reflectionListener.ListenerManager;
import ideaeclipse.reflectionListener.parents.Listener;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * TODO: Close connection if header isn't received within 1 second to avoid java connections
 * TODO: Auto loader to update LoadWorkDir's When a change is detected (https://stackoverflow.com/questions/23452527/watching-a-directory-for-changes-in-java)
 * This class handles all traffic and starts a service eventide a connection is started with the socket
 *
 * @author Myles T
 * @see ideaeclipse.JavaHttpServer.JavaHttpServer
 * @see LoadWorkDirData
 */
public class HttpServer {
    private final ListenerManager listenerManager = new ListenerManager();

    /**
     * All params are passed from {@link ideaeclipse.JavaHttpServer.JavaHttpServer}
     * This loads both passed directories and starts a ServerSocket, the port must be available or an error message
     * will be displayed
     *
     * @param listeners  listener classes for end points
     * @param port       port to listen on, default 80
     * @param service    thread dedication see java doc for info. Cached thread pool is generally recommended
     * @param privateDir private data directory
     * @param publicDir  public data directory
     */
    public HttpServer(final List<Listener> listeners, final int port, final ExecutorService service, final File privateDir, final File publicDir) {
        try {
            for (Listener listener : listeners) {
                this.listenerManager.registerListener(listener);
            }
            LoadWorkDirData privateData = new LoadWorkDirData(privateDir);
            LoadWorkDirData publicData = new LoadWorkDirData(publicDir);
            ServerSocket serverSocket = new ServerSocket(port);
            while (!service.isShutdown()) {
                service.submit(new InputHandler(listenerManager, serverSocket.accept(), privateData.getData(), publicData.getData()));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This class will load all files from a specific directory
     *
     * @author Myles T
     * @see HttpServer
     * @see ideaeclipse.JavaHttpServer.JavaHttpServer
     */
    private class LoadWorkDirData {
        private final String replace;
        private final Map<String, File> data;

        /**
         * This will load all files from a specific directory.
         * Key example:
         * If the directory is /home/user/dir
         * and you have a file with absolute directory /home/user/dir/test.pdf
         * the key would be test.pdf
         *
         * @param workDir directory to load data from
         */
        private LoadWorkDirData(final File workDir) {
            if (System.getProperty("os.name").contains("Windows"))
                replace = workDir.getAbsolutePath() + "\\";
            else
                replace = workDir.getAbsolutePath() + "/";
            this.data = getData(workDir);
        }

        /**
         * Recursively load all files into the map
         * Key is the work dir to load from
         * Value is the file object
         *
         * @param workDir directory to add from
         * @return completed map
         */
        private Map<String, File> getData(final File workDir) {
            Map<String, File> data = new HashMap<>();
            for (File file : Objects.requireNonNull(workDir.listFiles())) {
                if (file.isDirectory())
                    for (Map.Entry<String, File> entry : getData(file).entrySet())
                        data.put(entry.getKey(), entry.getValue());
                else
                    data.put(file.getAbsolutePath().replace(this.replace, ""), file);
            }
            return data;
        }

        /**
         * When initializing the method it will generate a map of key value pairs.
         * The key will be the directory and the value will be the java file object associated with that directory
         * This allows for quicker load times because everything is pre loaded on launch
         *
         * @return map of data
         */
        private Map<String, File> getData() {
            return data;
        }
    }

    /**
     * TODO: Debug mode
     * TODO: custom redirect
     * TODO: Dynamic Directories
     * This class is invoked every time a new connection is started
     *
     * @author Myles T
     * @see HttpServer
     */
    private static class InputHandler implements Runnable {
        private final ListenerManager manager;
        private final Socket socket;
        private final Map<String, File> privateData;
        private final Map<String, File> publicData;

        /**
         * Called from {@link HttpServer}
         *
         * @param manager     listmanager will all registered passed listeners
         * @param socket      socket that was generated apon connection
         * @param privateData map of all files in the privateData directory
         * @param publicData  map of all files in the publicData directory
         */
        InputHandler(final ListenerManager manager, final Socket socket, final Map<String, File> privateData, final Map<String, File> publicData) {
            this.manager = manager;
            this.socket = socket;
            this.privateData = privateData;
            this.publicData = publicData;
        }

        /**
         * TODO: Seperate parsing into methods to compartmentalize the connection process
         * TODO: check multiple End points on launch not every connection
         * TODO: Check that if the end point calls for a dynamic end point that it has the Dynamic Event param.
         * <p>
         * This method is invoked everytime an incoming connection is active
         * It parses the data using {@link InitialRequest} logs all data to screen
         * if the path requested is a public path then send the file to the user
         * if its a valid endpoint execute the end point
         * if its neither and the method is get redirect to "/" TODO: make this custom
         * else if its not a GET Request send a blank response TODO: make so you can redirect to a 404 endpoint to return json for example
         */
        @Override
        public void run() {
            try {
                IInitialRequest request = new InitialRequest(this.socket.getInputStream());
                final String directory = request.getDirectory();
                final RequestMethod method = request.getMethod();
                final Map<String, String> params = request.getParams();
                final String passedData = request.getPassedData();
                System.out.println("Directory: " + directory);
                System.out.println("Method: " + method);
                System.out.println("Params: " + params);
                System.out.println("Connection Address: " + request.getConnectionAddress());
                System.out.println("Passed Data: " + passedData);

                IResponse response = new Response(this.socket, this.privateData);

                //if file is public
                final String directoryParsed = directory.substring(1);
                if (this.publicData.get(directoryParsed) != null) {
                    System.out.println("Public data");
                    response.sendFile(200, this.publicData.get(directoryParsed));
                } else {
                    System.out.println("Method request");
                    ConnectionEvent event = new ConnectionEvent(response, request);
                    List<Executable> executableList = this.manager.getExecutablesByAnnotation(event, PageInfo.class, method, directory);
                    switch (executableList.size()) {
                        case 0:
                            if (method == RequestMethod.GET)
                                Util.redirect(this.socket, "/");
                            else
                                Util.blank(this.socket);
                            break;
                        case 1:
                            executableList.get(0).execute();
                            //TODO: allow for customizable response
                            if(!this.socket.isClosed()){
                                Util.blank(this.socket);
                            }
                            break;
                        default:
                            System.err.println("You have multiple end points defined for " + directory);
                            break;
                    }
                }
            } catch (IOException | EventNotFound e) {
                e.printStackTrace();
            }
        }

    }
}
