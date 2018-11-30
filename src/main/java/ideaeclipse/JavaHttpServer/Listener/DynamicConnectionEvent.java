package ideaeclipse.JavaHttpServer.Listener;

import ideaeclipse.JavaHttpServer.Parameters;
import ideaeclipse.JavaHttpServer.Writer;
import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.reflectionListener.annotations.ReturnHandler;
import ideaeclipse.reflectionListener.callByAnnotation.dynamicAnnotations.DynamicEvent;

@ReturnHandler(returnType = Boolean.class)
public class DynamicConnectionEvent extends DynamicEvent {
    private final String token;
    private final Json data;
    private final Parameters parameters;
    private final Writer printWriter;

    public DynamicConnectionEvent(final String dynamic,final String token, final String data, final Parameters parameters, final Writer writer) {
        super(dynamic);
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
