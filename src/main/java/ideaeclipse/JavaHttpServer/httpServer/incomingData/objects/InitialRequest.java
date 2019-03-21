package ideaeclipse.JavaHttpServer.httpServer.incomingData.objects;

import ideaeclipse.JavaHttpServer.httpServer.incomingData.IInitialRequest;
import ideaeclipse.JavaHttpServer.httpServer.responses.objects.RequestMethod;

import java.io.*;
import java.util.*;

/**
 * Takes the initial input data from the http request and parses it
 *
 * @author Myles T
 */
public class InitialRequest implements IInitialRequest {
    private final RequestMethod method;
    private final String directory;
    private final String connectionAddress;
    private final Map<String, String> params = new HashMap<>();
    private final Map<String, String> headerInfo = new HashMap<>();
    private final String passedData;

    /**
     * Parses the data into the method, directory and directory params
     * If the method isn't GET and has a content-length in the header
     * That x-amount of bytes is read and stored as a string.
     * The only data that is expected is json data, not files
     *
     * @param inputStream Input stream
     * @throws IOException error in reading data
     */
    public InitialRequest(final InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> input = new LinkedList<>();
        String readLine;
        while ((readLine = in.readLine()) != null) {
            if (readLine.isEmpty())
                break;
            input.add(readLine);
        }
        List<String> parsedLine1 = Arrays.asList(input.get(0).split(" "));
        this.method = RequestMethod.valueOf(parsedLine1.get(0));
        List<String> parsedDirectory = Arrays.asList(parsedLine1.get(1).split("\\?"));
        this.directory = (parsedDirectory.get(0).endsWith("/") && parsedDirectory.get(0).length() > 1 ? parsedDirectory.get(0).substring(0, parsedDirectory.get(0).length() - 1) : parsedDirectory.get(0));
        if (parsedDirectory.size() == 2) {
            for (String string : parsedDirectory.get(1).split("&")) {
                List<String> parsedValues = Arrays.asList(string.split("="));
                this.params.put(parsedValues.get(0), parsedValues.get(1));
            }
        }
        for (int i = 1; i < input.size(); i++) {
            String s = input.get(i);
            headerInfo.put(s.substring(0, s.indexOf(":")).trim(), s.substring(s.indexOf(":") + 1).trim());
        }
        this.connectionAddress = headerInfo.get("Host");
        if (this.method != RequestMethod.GET) {
            int size = 0;
            size = Integer.parseInt(headerInfo.get("Content-Length"));
            System.out.println("Loading Content with size: " + size);
            // Loads passed params
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size; i++)
                builder.append((char) in.read());
            if (builder.length() > 0)
                this.passedData = String.valueOf(builder);
            else
                this.passedData = null;
        } else
            this.passedData = null;

    }

    /**
     * @return Method
     */
    public RequestMethod getMethod() {
        return this.method;
    }

    /**
     * @return Directory
     */
    public String getDirectory() {
        return this.directory;
    }

    /**
     * @return connection address
     */
    public String getConnectionAddress() {
        return this.connectionAddress;
    }

    /**
     * @return Params
     */
    public Map<String, String> getParams() {
        return this.params;
    }

    /**
     * @return additional header info
     */
    public Map<String, String> getAdditionalHeaderInfo() {
        return this.headerInfo;
    }

    /**
     * @return Attached json string
     */
    public String getPassedData() {
        return passedData;
    }
}
