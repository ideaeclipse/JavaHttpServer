package JavaHttpServer.Html;

/**
 * Different possible response codes for html headers
 *
 * @author Ideaeclipse
 */
public enum ResponseCodes {
    Code_200("OK"),Code_401("Unauthorized"),Code_404("HtmlResponse Not Found");
    private String string;
    ResponseCodes(final String string){
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
