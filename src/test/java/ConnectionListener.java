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
    public Boolean connect(ConnectionEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Home page");
        return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "Page.html", map));
    }

    @EventHandler
    @PageData(directory = "/404")
    public Boolean connect404(ConnectionEvent event) {
        return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_404), "404Page.html"));
    }

    @EventHandler
    @PageData(directory = "/jsonTest")
    public Boolean jsonTest(ConnectionEvent event) {
        Json json = new Json();
        json.put("SomeKey", "SomeValue");
        return event.getWriter().sendPage(new JsonResponse(new Header(ResponseCodes.Code_200), json));
    }

    @EventHandler
    @PageData(method = PageData.Method.POST, directory = "/postTest")
    public Boolean postTest(ConnectionEvent event) {
        return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "PostTest.html"));
    }

    @EventHandler
    @PageData(directory = "/ejsTest")
    public Boolean ejsTest(ConnectionEvent event) {
        Mapper mapper = new Mapper();
        mapper.a = 11;
        mapper.string = "InsertString test";
        mapper.listName = Arrays.asList(new HtmlResponse("ejsTest/Test1.html").getResponse(), new HtmlResponse("ejsTest/Test2.html").getResponse());
        return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "ejsTest/ejsTest.html", Builder.buildData(mapper)));
    }

    @EventHandler
    @PageData(directory = "/bootstrapTest")
    public Boolean bootstrapTest(ConnectionEvent event) {
        return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "BootStrapTest.html"));
    }
    /*
    @EventHandler
    @PageData(directory = "/latex")
    public Boolean latex(ConnectionEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("string", event.getParameters().getMap().get("string").replace("%20", " "));
        System.out.println(map);
        //\[ x^n + y^n = z^n \]
        return event.getWriter().sendPage(new HtmlResponse(new Header(ResponseCodes.Code_200), "latex.html", map));
    }
    */

    public static class Mapper {
        public Integer a;
        public String string;
        public List<String> listName;
    }
}

