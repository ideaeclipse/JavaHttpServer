import ideaeclipse.JavaHttpServer.JavaHttpServer;

import java.io.IOException;
import java.util.concurrent.Executors;

public class main {
    public static void main(String[] args) throws IOException {
        new JavaHttpServer().executor(Executors.newWorkStealingPool()).registerEndpoints(new Endpoint1()).workDirectory("private").publicDirectory("public").start();
    }
}
