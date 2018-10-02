package ideaeclipse.JavaHttpServer.Listener;

import ideaeclipse.JavaHttpServer.Parameters;
import ideaeclipse.JavaHttpServer.Writer;
import ideaeclipse.reflectionListener.Event;

/**
 * This is the event that extends {@link Event}
 * This is the event that every method in your supplied listener should contain
 *
 * @author Ideaeclipse
 */
public class ConnectionEvent extends Event {
    private final Parameters parameters;
    private final Writer printWriter;

    /**
     * @param parameters
     * @param writer
     */
    public ConnectionEvent(final Parameters parameters, final Writer writer) {
        this.parameters = parameters;
        this.printWriter = writer;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public Writer getWriter() {
        return printWriter;
    }
}
