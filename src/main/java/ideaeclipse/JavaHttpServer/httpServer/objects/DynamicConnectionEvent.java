package ideaeclipse.JavaHttpServer.httpServer.objects;

import ideaeclipse.JavaHttpServer.httpServer.incomingData.IInitialRequest;
import ideaeclipse.JavaHttpServer.httpServer.responses.IResponse;
import ideaeclipse.reflectionListener.parents.Event;

import java.util.Map;

/**
 * Used when the endpoint requires dynamic variables
 * Example:
 * Regular endpoint: /getUsers
 * Dynamic endpoint: /users/{id}
 * The returns map of data will have 1 key which will be id
 *
 * @author Myles T
 */
public class DynamicConnectionEvent extends Event {
    private final IResponse response;
    private final IInitialRequest request;
    private final Map<String, String> dynamicData;

    /**
     * @param response Allows you to create a response to send when an endpoint is called
     * @param request  all info gather from header
     */
    DynamicConnectionEvent(final IResponse response, final IInitialRequest request, final Map<String, String> dynamicData) {
        this.response = response;
        this.request = request;
        this.dynamicData = dynamicData;
    }

    /**
     * See README for examples
     *
     * @return get response object
     */
    public IResponse getResponse() {
        return response;
    }

    /**
     * See README for examples
     *
     * @return get request object
     */
    public IInitialRequest getRequest() {
        return request;
    }

    /**
     * Get the dynamic data from the param
     *
     * @return map of dynamic data
     */
    public Map<String, String> getDynamicData() {
        return dynamicData;
    }
}
