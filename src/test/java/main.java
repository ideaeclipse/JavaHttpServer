import ideaeclipse.JavaHttpServer.JavaHttpServer;

import java.io.IOException;
import java.util.concurrent.Executors;

public class main {
    public static void main(String[] args){
        new JavaHttpServer().executor(Executors.newCachedThreadPool()).registerEndpoints(new Endpoint1()).privateDirectory("private").publicDirectory("public").start();
    }
}
