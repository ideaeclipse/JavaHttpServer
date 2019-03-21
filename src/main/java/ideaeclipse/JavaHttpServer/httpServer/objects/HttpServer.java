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
import ideaeclipse.reflectionListener.annotations.CallableEvent;
import ideaeclipse.reflectionListener.parents.Listener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * TODO: Reflection listener superClass for Event, loop till its found or java.lang.Object is hit
 * TODO: Check size of params in Reflection listener lib ***********************************
 * TODO: Close connection if header isn't received within 1 second to avoid java connections
 * TODO: Auto loader to update LoadWorkDir's When a change is detected (https://stackoverflow.com/questions/23452527/watching-a-directory-for-changes-in-java)
 * TODO: * end-point accept all traffic
 * This class handles all traffic and starts a service eventide a connection is started with the socket
 *
 * @author Myles T
 * @see ideaeclipse.JavaHttpServer.JavaHttpServer
 * @see LoadWorkDirData
 */
public class HttpServer {
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
            final List<String> dynamicDirectories = new LinkedList<>();
            if (checkEndPoints(listeners, dynamicDirectories)) {
                ListenerManager listenerManager = new ListenerManager();
                for (Listener listener : listeners) {
                    listenerManager.registerListener(listener);
                }
                LoadWorkDirData privateData = new LoadWorkDirData(privateDir);
                LoadWorkDirData publicData = new LoadWorkDirData(publicDir);
                ServerSocket serverSocket = new ServerSocket(port);
                while (!service.isShutdown()) {
                    service.submit(new InputHandler(listenerManager, serverSocket.accept(), privateData.getData(), publicData.getData(), dynamicDirectories));
                }
            } else {
                if (Thread.currentThread().getName().equals("main"))
                    System.exit(-1);
                else
                    Thread.currentThread().stop();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method is used to check all the endpoints for validity
     * For all endpoints they must have 2 annotations, {@link PageInfo} and {@link CallableEvent}
     * If the endpoint is static it must have the method parameter of type {@link ConnectionEvent}
     * If the endpoint is dynamic it must have the method parameter of type {@link DynamicConnectionEvent}
     *
     * @param listeners list of literes passed from {@link ideaeclipse.JavaHttpServer.JavaHttpServer}
     * @return true if all tests pass for each endpoint, false if one test fails
     */
    private boolean checkEndPoints(final List<Listener> listeners, final List<String> dynamicDirectories) {
        final List<String> endpoints = new LinkedList<>();
        int staticEndpoints = 0;
        int dynamicEndpoints = 0;
        for (Listener listener : listeners) {
            for (Method method : listener.getClass().getDeclaredMethods()) {
                PageInfo info = method.getAnnotation(PageInfo.class);
                if (info != null && method.getAnnotation(CallableEvent.class) != null) {
                    if (info.directory().contains("{") && info.directory().contains("}")) {
                        if (method.getParameterCount() == 1) {
                            if (method.getParameterTypes()[0].equals(DynamicConnectionEvent.class)) {
                                if (info.directory().chars().filter(ch -> ch == '{').count() == info.directory().chars().filter(ch -> ch == '}').count() && checkDynamicDirectory(info.directory())) {
                                    System.out.println("Dynamic end-point registered: Method: " + method.getName() + " in: " + listener.getClass() + " with endpoint: " + info.directory() + " with dynamic keys: " + Arrays.toString(Arrays.stream(info.directory().split("[\\\\{}]")).filter(o -> !o.startsWith("/")).toArray()) + " and method: " + info.method());
                                    dynamicDirectories.add((info.directory().endsWith("/") ? info.directory().substring(0, info.directory().length() - 1) : info.directory()));
                                    endpoints.add(info.directory());
                                    dynamicEndpoints++;
                                } else {
                                    System.err.println("ERROR: Method: " + method.getName() + " in: " + listener.getClass() + " with endpoint: " + info.directory() + " and method: " + info.method());
                                    System.err.println("ERROR: Your formatting for the dynamic directory is incorrect see documentation");
                                    return false;
                                }
                            } else {
                                System.err.println("ERROR: Method: " + method.getName() + " in: " + listener.getClass() + " with endpoint: " + info.directory() + " and method: " + info.method());
                                System.err.println("ERROR: You can't have a static directory with a ConnectionEvent, change this to a DynamicConnectionEvent object or change your end point to a static one");
                                return false;
                            }
                        } else {
                            System.err.println("ERROR: Method: " + method.getName() + " in: " + listener.getClass() + " with endpoint: " + info.directory() + " and method: " + info.method());
                            System.err.println("ERROR: You must have a DynamicConnectionEvent object as your endpoint parameter");
                            return false;
                        }
                    } else {
                        if (method.getParameterCount() == 1) {
                            if (method.getParameterTypes()[0].equals(ConnectionEvent.class)) {
                                System.out.println("Static end-point registered: Method: " + method.getName() + " in: " + listener.getClass() + " with endpoint: " + info.directory() + " and method: " + info.method());
                                endpoints.add(info.directory());
                                staticEndpoints++;
                            } else {
                                System.err.println("ERROR: Method: " + method.getName() + " in: " + listener.getClass() + " with endpoint: " + info.directory() + " and method: " + info.method());
                                System.err.println("ERROR: You can't have a static directory with a DynamicConnectionEvent, change this to a ConnectionEvent object or change your end point to a dynamic one");
                                return false;
                            }
                        } else {
                            System.err.println("ERROR: Method: " + method.getName() + " in: " + listener.getClass() + " with endpoint: " + info.directory() + " and method: " + info.method());
                            System.err.println("ERROR: You must have a ConnectionEvent object as your endpoint parameter");
                            return false;
                        }
                    }
                }
            }
        }
        List<Object> duplicates = Arrays.asList(endpoints.stream().filter(e -> Collections.frequency(endpoints, e) > 1).distinct().toArray());
        if (duplicates.size() > 0) {
            System.err.println("ERROR: You have duplicate endpoints, there is not support for duplicate endpoints please remove one");
            System.err.println("ERROR: They are: " + duplicates);
            return false;
        }
        System.out.format("There are %d static endpoints with %d dynamic endpoints\n", staticEndpoints, dynamicEndpoints);
        System.out.println("If you where expecting other endpoints to be loaded, ensure you passed the end point class to the register, and ensure the methods have the proper annotations");
        System.out.println("Your end points are loaded correctly, Starting HttpServer");
        return true;
    }

    /**
     * Loops through a PageInfo directory string.
     * open being true means that the function is expecting a open brace
     * close being true means that the function is expecting a close brace
     * if open is true close is false and vice versa
     * if the function encounters a open brace when it was expecting a close the formatting is wrong, returns false
     * if the function encounters a close brace when it was expecting a open the formatting is wrong, returns false
     * if the loop can finish succesffully then the formatting is correct and will return true
     *
     * @param string directory string
     * @return true if formatting is correct, false if formatting is inccorrect
     */
    @SuppressWarnings("ALL")
    private boolean checkDynamicDirectory(final String string) {
        boolean open = true;
        boolean close = false;
        for (char c : string.toCharArray()) {
            if (c == '{') {
                if (open) {
                    open = false;
                    close = true;
                } else if (close)
                    return false;
            } else if (c == '}') {
                if (close) {
                    close = false;
                    open = true;
                } else if (open)
                    return false;
            }
        }
        return true;
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
        private final List<String> dynamicDirectories;

        /**
         * Called from {@link HttpServer}
         *
         * @param manager     listmanager will all registered passed listeners
         * @param socket      socket that was generated apon connection
         * @param privateData map of all files in the privateData directory
         * @param publicData  map of all files in the publicData directory
         */
        InputHandler(final ListenerManager manager, final Socket socket, final Map<String, File> privateData, final Map<String, File> publicData, final List<String> dynamicDirectories) {
            this.manager = manager;
            this.socket = socket;
            this.privateData = privateData;
            this.publicData = publicData;
            this.dynamicDirectories = dynamicDirectories;
        }

        /**
         * TODO: check multiple End points on launch not every connection
         * TODO: Check that if the end point calls for a dynamic end point that it has the Dynamic Event param.
         * <p>
         * This method is invoked every time an incoming connection is active
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
                    HashMap<String, String> dynamicData = new HashMap<>();
                    String s = isDynamic(directory, dynamicData);
                    if (s.length() == 0) {
                        System.out.println("End point requested is static: " + directory);
                        ConnectionEvent event = new ConnectionEvent(response, request);
                        List<Executable> executableList = this.manager.getExecutablesByAnnotation(event, PageInfo.class, method, directory);
                        execute(executableList, method, directory);
                    } else {
                        System.out.println("End point requested is dynamic: " + s);
                        DynamicConnectionEvent event = new DynamicConnectionEvent(response, request, dynamicData);
                        List<Executable> executableList = this.manager.getExecutablesByAnnotation(event, PageInfo.class, method, s);
                        execute(executableList, method, s);

                    }
                }
            } catch (IOException | EventNotFound e) {
                e.printStackTrace();
            }
        }

        /**
         * This method checks to see if a directory that was requested matches with one of the dynamic directories declared
         * inside the endpoint listener files.
         * The dynamicDirectories where stored when the server started up
         * First step is to compare the number of forward slashes with the dynamicData if they are equal then check to see
         * if all the values that aren't inside the curly braces are contained inside the passed request. If they are
         * then split all of them into a map and return the dynamicModel that was it. If one doesn't exist a string of
         * length 0 will be returned
         *
         * @param request     directory requested by the user
         * @param dynamicData where to store data
         * @return dynamic directory endpoint from inside the endpoint file(s)
         */
        private String isDynamic(final String request, final Map<String, String> dynamicData) {
            String data = "";
            outer:
            for (String dynamicModel : dynamicDirectories) {
                if (dynamicModel.chars().filter(ch -> ch == '/').count() == request.chars().filter(ch -> ch == '/').count()) {
                    data = dynamicModel;
                    for (String temp : dynamicModel.split("[\\\\{}]")) {
                        if (temp.startsWith("/") && temp.endsWith("/")) {
                            if (!request.contains(temp))
                                continue outer;
                        }
                    }
                    for (String temp : dynamicModel.split("[\\\\{}]")) {
                        if (temp.startsWith("/") && temp.endsWith("/")) {
                            String dTemp = dynamicModel.substring(dynamicModel.indexOf(temp) + temp.length());
                            String rTemp = request.substring(request.indexOf(temp) + temp.length());
                            dTemp = (dTemp.contains("/") ? dTemp.substring(0, dTemp.indexOf("/")) : dTemp);
                            rTemp = (rTemp.contains("/") ? rTemp.substring(0, rTemp.indexOf("/")) : rTemp);
                            dTemp = dTemp.substring(1, dTemp.length() - 1);
                            dynamicData.put(dTemp, rTemp);
                        }
                    }
                    break;
                }
            }
            return data;
        }

        /**
         * Executes a directory if one exists, else return a blank one or redirect to /
         *
         * @param executableList list of executables see {@link InputHandler}
         * @param method         i.e. GET see {@link RequestMethod}
         * @param directory      directory model from file
         * @throws IOException   if data can't be written to socket
         * @throws EventNotFound if the event can't be found
         */
        private void execute(final List<Executable> executableList, final RequestMethod method, final String directory) throws IOException, EventNotFound {
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
                    if (!this.socket.isClosed()) {
                        Util.blank(this.socket);
                    }
                    break;
                default:
                    System.err.println("You have multiple end points defined for " + directory);
                    break;
            }
        }

    }
}
