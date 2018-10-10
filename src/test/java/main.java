import ideaeclipse.JavaHttpServer.Server;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Desktop desktop = java.awt.Desktop.getDesktop();
        URI oURL = new URI("http://localhost:8080/?key1=5&key2=string");
        desktop.browse(oURL);
        new Server(8080, new ConnectionListener());
    }
}

