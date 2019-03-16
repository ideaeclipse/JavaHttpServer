package ideaeclipse.JavaHttpServer;

import ideaeclipse.JavaHttpServer.httpServer.HttpServer;
import ideaeclipse.reflectionListener.parents.Listener;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaHttpServer {
    private List<Listener> listenerList;
    private int port;
    private ExecutorService service;
    private File privateDir;
    private File publicDir;

    public JavaHttpServer registerEndpoints(final Listener... listeners) {
        this.listenerList = Arrays.asList(listeners);
        return this;
    }

    public JavaHttpServer port(final int port) {
        this.port = port;
        return this;
    }

    public JavaHttpServer executor(final ExecutorService service) {
        this.service = service;
        return this;
    }

    public JavaHttpServer workDirectory(final String directory) {
        File workDir = new File(directory);
        if (workDir.isDirectory() && workDir.exists()) {
            this.privateDir = workDir;
        } else
            System.err.println("private dir passed doesn't exist: " + workDir.getAbsolutePath() + " cannot start web server without a valid work dir");
        return this;
    }
    public JavaHttpServer publicDirectory(final String directory){
        File workDir = new File(directory);
        if (workDir.isDirectory() && workDir.exists()) {
            this.publicDir = workDir;
        } else
            System.err.println("public dir passed doesn't exist: " + workDir.getAbsolutePath() + " cannot start web server without a valid work dir");
        return this;
    }

    public HttpServer start() throws IOException {
        if(this.listenerList == null)
            this.listenerList = new LinkedList<>();
        if (this.port == 0)
            this.port = 80;
        if (this.service == null)
            this.service = Executors.newCachedThreadPool();
        if (this.privateDir == null)
            System.exit(-1);
        if(this.publicDir == null)
            System.exit(-1);
        return new HttpServer(this.listenerList, this.port, this.service, this.privateDir, this.publicDir);
    }
}
