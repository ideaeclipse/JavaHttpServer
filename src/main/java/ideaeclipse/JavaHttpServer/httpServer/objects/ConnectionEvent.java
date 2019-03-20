package ideaeclipse.JavaHttpServer.httpServer.objects;

import ideaeclipse.JavaHttpServer.httpServer.incomingData.IInitialRequest;
import ideaeclipse.JavaHttpServer.httpServer.responses.IResponse;
import ideaeclipse.JavaHttpServer.httpServer.responses.objects.RequestMethod;
import ideaeclipse.reflectionListener.parents.Event;

import java.util.Map;

/**
 * This class is the class that holds all public data you have access to.
 * See README for examples of how to implement this for your endpoints
 * This is an endpoint param for when your endpoint is static
 *
 * @author Myles T
 */
public class ConnectionEvent extends Event {
    private final IResponse response;
    private final IInitialRequest request;

    /**
     * @param response Allows you to create a response to send when an endpoint is called
     * @param request all info gather from header
     */
    public ConnectionEvent(final IResponse response, final IInitialRequest request) {
        this.response = response;
        this.request = request;

    }

    /**
     * See README for examples
     * @return get response object
     */
    public IResponse getResponse() {
        return response;
    }
    /**
     * See README for examples
     * @return get request object
     */
    public IInitialRequest getRequest() {
        return request;
    }
}
