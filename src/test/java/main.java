import ideaeclipse.JavaHttpServer.Listener.RequiresAuthorization;
import ideaeclipse.JavaHttpServer.Server;
import ideaeclipse.reflectionListener.Listener;

import java.io.IOException;
import java.util.Objects;

public class main {
    public static void main(String[] args) throws IOException {
        /*
        Desktop desktop = java.awt.Desktop.getDesktop();
        URI oURL = new URI("http://localhost:8080/?key1=5&key2=string");
        desktop.browse(oURL);
        */
        Listener listener = new ConnectionListener();
        RequiresAuthorization check = new RequiresAuthorization(listener) {

            @Override
            public Boolean handleToken(String token) {
                return Objects.equals(token,"token");
            }
        };
        new Server(8080,listener, check);
    }
}

