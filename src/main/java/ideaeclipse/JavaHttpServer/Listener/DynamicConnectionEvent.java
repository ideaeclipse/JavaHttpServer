package ideaeclipse.JavaHttpServer.Listener;

import ideaeclipse.JavaHttpServer.Parameters;
import ideaeclipse.JavaHttpServer.Writer;
import ideaeclipse.reflectionListener.annotations.ReturnHandler;

@ReturnHandler(returnType = Boolean.class)
public class DynamicConnectionEvent extends ConnectionEvent {
   private final String dynamicValue;
    public DynamicConnectionEvent(final String dynamic,final String token, final String data, final Parameters parameters, final Writer writer) {
        super(token,data,parameters,writer);
        this.dynamicValue = dynamic;
    }

    public String getDynamicValue() {
        return dynamicValue;
    }
}
