package JavaHttpServer;

import java.util.StringTokenizer;

/**
 * This class pareses the request header sent from the user
 * It parses it into a file path,method, and obtains parameters if there are any
 *
 * @author Ideaeclipse
 */
class HttpHeader {
    private String filePath;
    private final String method;
    private final Parameters parameters;

    /**
     * method will always be the first token
     * params will always be the second token
     * based on the params you parse out the filepath and if there is a ? and the ? is the last char there is parameters
     *
     * @param parameters tokenizer of parameters
     */
    HttpHeader(final StringTokenizer parameters) {
        this.method = parameters.nextToken();
        String params = parameters.nextToken();
        if (params.indexOf('?') > 0 && params.indexOf('?') != params.length() - 1) {
            this.filePath = params.substring(0, params.indexOf('?'));
            this.parameters = new Parameters(params);
        } else {
            this.filePath = params;
            this.parameters = null;
        }
    }

    /**
     * @return the request method
     */
    String getMethod() {
        return method;
    }

    /**
     * @return the file path
     */
    String getFilePath() {
        return filePath;
    }

    /**
     * @return parameters, can be null
     */
    Parameters getParameters() {
        return parameters;
    }
}
