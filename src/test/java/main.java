import ideaeclipse.JavaHttpServer.JavaHttpServer;

import java.io.IOException;
import java.util.concurrent.Executors;

public class main {
    public static void main(String[] args) {
        Thread one = new Thread(() -> new JavaHttpServer().
                executor(Executors.newCachedThreadPool()).
                registerEndpoints(new Endpoint1()).
                privateDirectory("private").
                publicDirectory("public").
                start());
        one.start();
        try {
            one.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("Test 1 complete Thread killed");
    }
}
