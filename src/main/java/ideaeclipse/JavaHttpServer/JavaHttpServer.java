package ideaeclipse.JavaHttpServer;

import ideaeclipse.JavaHttpServer.httpServer.objects.HttpServer;
import ideaeclipse.reflectionListener.parents.Listener;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the class the user should initialize to use the lib
 * see method descriptions for use cases
 *
 * @author Myles T
 * @see HttpServer
 */
public class JavaHttpServer {
    private List<Listener> listenerList;
    private int port;
    private ExecutorService service;
    private File privateDir;
    private File publicDir;

    /**
     * Used to register end point classes, must implement {@link Listener}
     *
     * @param listeners listeners to add
     * @return instance of JavaHttpServer
     */
    public JavaHttpServer registerEndpoints(final Listener... listeners) {
        this.listenerList = Arrays.asList(listeners);
        return this;
    }

    /**
     * This will set the port of the web server, if not called default port is 80
     * port must be available on the system
     *
     * @param port port to set
     * @return instance of JavaHttpServer
     */
    public JavaHttpServer port(final int port) {
        this.port = port;
        return this;
    }

    /**
     * Thread pool. This allows you to dedicate a certain number of threads to this server
     * Default is {@link Executors#newCachedThreadPool()}
     *
     * @param service service to set
     * @return instance of JavaHttpServer
     */
    public JavaHttpServer executor(final ExecutorService service) {
        this.service = service;
        return this;
    }

    /**
     * This is a directory where all your html files are stored
     * Assume the port is 80
     * If you where to call for an html file lik http://localhost/test.html
     * where the file is test.html you would get a 404 because these files are required to be delivered through
     * end points. If you want direct html access you need to put the html file in the public directory but
     * I would advice creating generic end points to allow for pragmatical differences between sessions.
     *
     * @param directory private directory
     * @return instance of JavaHttpServer
     * @see ideaeclipse.JavaHttpServer.httpServer.objects.HttpServer.LoadWorkDirData
     */
    public JavaHttpServer privateDirectory(final String directory) {
        File workDir = new File(directory);
        if (workDir.isDirectory() && workDir.exists()) {
            this.privateDir = workDir;
        } else
            System.err.println("private dir passed doesn't exist: " + workDir.getAbsolutePath() + " cannot start web server without a valid work dir");
        return this;
    }

    /**
     * This is a directory where all your other files are located.
     * For example any file you want to display through direct access, like http://localhost/test.pdf
     * This will load the test.pdf file in the declared public directory
     *
     * @param directory destination of the public directory
     * @return instance of JavaHttpServer
     * @see ideaeclipse.JavaHttpServer.httpServer.objects.HttpServer.LoadWorkDirData
     */
    public JavaHttpServer publicDirectory(final String directory) {
        File workDir = new File(directory);
        if (workDir.isDirectory() && workDir.exists()) {
            this.publicDir = workDir;
        } else
            System.err.println("public dir passed doesn't exist: " + workDir.getAbsolutePath() + " cannot start web server without a valid work dir");
        return this;
    }

    /**
     * This will start the server.
     * This is to be called once all your settings are finalized
     *
     * @return HttpServer instances
     */
    public HttpServer start() {
        if (this.listenerList == null) {
            System.err.println("You must pass at least 1 endpoint class object, see documentation on github");
            System.exit(-1);
        }
        if (this.port == 0)
            this.port = 80;
        if (this.service == null)
            this.service = Executors.newCachedThreadPool();
        if (this.privateDir == null) {
            System.err.println("You didn't pass a private dir, see documentation on github");
            System.exit(-1);
        }
        if (this.publicDir == null) {
            System.err.println("You didn't pass a public dir, see documentation on github");
            System.exit(-1);
        }
        return new HttpServer(this.listenerList, this.port, this.service, this.privateDir, this.publicDir);
    }
}
