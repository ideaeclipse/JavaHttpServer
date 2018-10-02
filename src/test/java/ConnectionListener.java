
import ideaeclipse.JavaHttpServer.Html.Header;
import ideaeclipse.JavaHttpServer.Html.HtmlResponse;
import ideaeclipse.JavaHttpServer.Html.JsonResponse;
import ideaeclipse.JavaHttpServer.Html.ResponseCodes;
import ideaeclipse.JavaHttpServer.Listener.ConnectionEvent;
import ideaeclipse.JavaHttpServer.Listener.PageData;
import ideaeclipse.JsonUtilities.Builder;
import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.reflectionListener.EventHandler;
import ideaeclipse.reflectionListener.Listener;

import java.util.*;

public class ConnectionListener implements Listener {
    @EventHandler
    @PageData
    public void connect(ConnectionEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Home page");
        event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "Page.html", map));
    }

    @EventHandler
    @PageData(directory = "/404")
    public void connect404(ConnectionEvent event) {
        event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_404), "404Page.html"));
    }

    @EventHandler
    @PageData(directory = "/jsonTest")
    public void jsonTest(ConnectionEvent event) {
        Json json = new Json();
        json.put("SomeKey", "SomeValue");
        event.getWriter().sendPage(new JsonResponse(new Header(ResponseCodes.Code_200), json));
    }

    @EventHandler
    @PageData(method = PageData.Method.POST, directory = "/postTest")
    public void postTest(ConnectionEvent event) {
        event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "PostTest.html"));
    }

    @EventHandler
    @PageData(directory = "/ejsTest")
    public void ejsTest(ConnectionEvent event) {
        Mapper mapper = new Mapper();
        mapper.a = 11;
        mapper.string = "InsertString test";
        mapper.listName = Arrays.asList(new HtmlResponse("ejsTest/Test1.html").getResponse(), new HtmlResponse("ejsTest/Test2.html").getResponse());
        event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "ejsTest/ejsTest.html", Builder.buildData(mapper)));
    }

    @EventHandler
    @PageData(directory = "/bootstrapTest")
    public void bootstrapTest(ConnectionEvent event) {
        event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "BootStrapTest.html"));
    }

    public static class Mapper {
        public Integer a;
        public String string;
        public List<String> listName;
    }
}
