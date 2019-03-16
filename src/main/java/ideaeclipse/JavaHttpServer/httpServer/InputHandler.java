package ideaeclipse.JavaHttpServer.httpServer;

import ideaeclipse.JavaHttpServer.httpServer.incomingData.InitialRequest;
import ideaeclipse.JavaHttpServer.httpServer.responses.RequestMethod;
import ideaeclipse.JavaHttpServer.httpServer.responses.Response;
import ideaeclipse.reflectionListener.Exceptions.EventNotFound;
import ideaeclipse.reflectionListener.Executable;
import ideaeclipse.reflectionListener.ListenerManager;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class InputHandler implements Runnable {
    private final ListenerManager manager;
    private final Socket socket;
    private final Map<String, File> privateData;
    private final Map<String, File> publicData;

    InputHandler(final ListenerManager manager, final Socket socket, final Map<String, File> privateData, final Map<String, File> publicData) {
        this.manager = manager;
        this.socket = socket;
        this.privateData = privateData;
        this.publicData = publicData;
    }

    @Override
    public void run() {
        try {
            InitialRequest request = new InitialRequest(this.socket.getInputStream());
            final String directory = request.getDirectory();
            final RequestMethod method = request.getMethod();
            final Map<String, String> params = request.getParams();
            final String passedData = request.getPassedData();
            System.out.println("Directory: " + directory);
            System.out.println("Method: " + method);
            System.out.println("Params: " + params);
            System.out.println("Passed Data: " + passedData);

            Response response = new Response(this.socket, this.privateData);

            //if file is public
            final String directoryParsed = directory.substring(1);
            if (this.publicData.get(directoryParsed) != null) {
                System.out.println("Public data");
                response.sendFile(200, this.publicData.get(directoryParsed));
            } else {
                System.out.println("Method request");
                ConnectionEvent event = new ConnectionEvent(response, directory, method, params);
                List<Executable> executableList = this.manager.getExecutablesByAnnotation(event, PageInfo.class, method, directory);
                switch (executableList.size()) {
                    case 0:
                        if (method == RequestMethod.GET)
                            Util.redirect(this.socket, "/");
                        else
                            Util.blank(this.socket);
                        break;
                    case 1:
                        executableList.get(0).execute();
                        break;
                    default:
                        System.err.println("You have multiple end points defined for " + directory);
                        break;
                }
            }

        } catch (IOException | EventNotFound e) {
            e.printStackTrace();
        }
    }

}
