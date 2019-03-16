import ideaeclipse.JavaHttpServer.httpServer.ConnectionEvent;
import ideaeclipse.JavaHttpServer.httpServer.PageInfo;
import ideaeclipse.reflectionListener.annotations.CallableEvent;
import ideaeclipse.reflectionListener.parents.Listener;

import java.io.IOException;

public class Endpoint1 implements Listener {

    @CallableEvent
    @PageInfo
    public void test(final ConnectionEvent event) {
        try {
            event.getResponse().sendFile(200,"resume.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
