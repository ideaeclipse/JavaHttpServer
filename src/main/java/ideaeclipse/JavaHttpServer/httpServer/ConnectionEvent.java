package ideaeclipse.JavaHttpServer.httpServer;

import ideaeclipse.JavaHttpServer.httpServer.responses.RequestMethod;
import ideaeclipse.JavaHttpServer.httpServer.responses.Response;
import ideaeclipse.reflectionListener.parents.Event;

import java.util.Map;

public class ConnectionEvent extends Event {
    private final Response response;
    private final String directory;
    private final RequestMethod method;
    private final Map<String, String> params;

    public ConnectionEvent(final Response response, final String directory, final RequestMethod method, final Map<String,String> params) {
        this.response = response;
        this.directory = directory;
        this.method = method;
        this.params = params;
    }

    public Response getResponse() {
        return response;
    }

    public String getDirectory() {
        return directory;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
