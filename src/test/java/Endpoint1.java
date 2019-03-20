import ideaeclipse.JavaHttpServer.httpServer.objects.ConnectionEvent;
import ideaeclipse.JavaHttpServer.httpServer.PageInfo;
import ideaeclipse.JavaHttpServer.httpServer.responses.objects.RequestMethod;
import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.reflectionListener.annotations.CallableEvent;
import ideaeclipse.reflectionListener.parents.Listener;

import java.io.IOException;

public class Endpoint1 implements Listener {

    @CallableEvent
    @PageInfo
    public void test(final ConnectionEvent event) {
        try {
            event.getResponse().sendFile(200, "resume.html");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @CallableEvent
    @PageInfo(method = RequestMethod.GET, directory = "/jsonResponse")
    public void test2(final ConnectionEvent event) {
        try {
            event.getResponse().sendJson(200, String.valueOf(new Json("{response:json}")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @CallableEvent
    @PageInfo(method = RequestMethod.POST, directory = "/jsonGet")
    public void test3(final ConnectionEvent event) {
        try {
            String s = event.getRequest().getPassedData();
            if (s.length() > 0)
                event.getResponse().sendJson(200, String.valueOf(new Json(event.getRequest().getPassedData())));
            else
                event.getResponse().sendJson(404, String.valueOf(new Json("{response:empty}")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
