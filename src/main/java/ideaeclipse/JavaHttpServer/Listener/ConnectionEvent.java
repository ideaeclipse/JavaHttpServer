package ideaeclipse.JavaHttpServer.Listener;

import ideaeclipse.JavaHttpServer.Parameters;
import ideaeclipse.JavaHttpServer.Writer;
import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.reflectionListener.Event;
import ideaeclipse.reflectionListener.ReturnHandler;

/**
 * This is the event that extends {@link Event}
 * This is the event that every method in your supplied listener should contain
 *
 * @author Ideaeclipse
 */
@ReturnHandler(returnType = Boolean.class)
public class ConnectionEvent extends Event {
    private final String token;
    private final Json data;
    private final Parameters parameters;
    private final Writer printWriter;

    /**
     * @param parameters
     * @param writer
     */
    public ConnectionEvent(final String token, final String data, final Parameters parameters, final Writer writer) {
        this.token = token;
        this.data = data == null ? new Json() : new Json(data);
        this.parameters = parameters;
        this.printWriter = writer;
    }

    public String getToken() {
        return token;
    }

    public Json getData() {
        return data;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public Writer getWriter() {
        return printWriter;
    }
}
