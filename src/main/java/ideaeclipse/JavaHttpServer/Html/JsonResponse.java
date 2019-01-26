package ideaeclipse.JavaHttpServer.Html;

import ideaeclipse.JsonUtilities.Json;

/**
 * This class allows a user to send a json response instead of a html response
 *
 * @see Response
 * @author Ideaeclipse
 */
public class JsonResponse implements Response{
    private final Header header;
    private final Json json;

    public JsonResponse(final ResponseCodes code, final Json json) {
        this.header = new Header(code);
        this.json = json;
    }

    @Override
    public String getResponse() {
        return header.getHeader() + json.toString();
    }
}
