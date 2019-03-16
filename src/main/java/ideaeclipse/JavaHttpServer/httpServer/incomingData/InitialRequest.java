package ideaeclipse.JavaHttpServer.httpServer.incomingData;

import ideaeclipse.JavaHttpServer.httpServer.responses.RequestMethod;

import java.io.*;
import java.util.*;

/**
 * Takes the initial input data from the http request and parses it
 *
 * @author Ideaeclipse
 */
public class InitialRequest {
    private final RequestMethod method;
    private final String directory;
    private final Map<String, String> params = new HashMap<>();
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
        this.directory = parsedDirectory.get(0);
        if (parsedDirectory.size() == 2) {
            for (String string : parsedDirectory.get(1).split("&")) {
                List<String> parsedValues = Arrays.asList(string.split("="));
                this.params.put(parsedValues.get(0), parsedValues.get(1));
            }
        }
        if (this.method != RequestMethod.GET) {
            int size = 0;
            for (String s : input) {
                if (s.contains("Content-Length: ")) {
                    size = Integer.parseInt(s.replace("Content-Length: ", ""));
                }
            }
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
     * @return Params
     */
    public Map<String, String> getParams() {
        return this.params;
    }

    /**
     * @return Attached json string
     */
    public String getPassedData() {
        return passedData;
    }
}
