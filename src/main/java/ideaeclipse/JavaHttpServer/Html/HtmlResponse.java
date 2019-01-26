package ideaeclipse.JavaHttpServer.Html;

import ideaeclipse.JavaHttpServer.Server;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class allows a user to formulate an html response
 *
 * @author Ideaeclipse
 */
public class HtmlResponse implements Response {
    private final Header header;
    private final File file;
    private final StringBuilder builder;
    private final Map<String, Object> map;

    /**
     * @param code     response code
     * @param fileName html file name in directory "resourceDirectory"
     * @param map      CustomEjs map of data you need to import into your file
     */
    public HtmlResponse(final ResponseCodes code, final String fileName, final Map<String, Object> map) {
        this.header = new Header(code);
        this.file = new File(Server.properties.getProperty("resourceDirectory") + "/" + fileName);
        this.map = map;
        this.builder = new StringBuilder();
    }

    /**
     * @param code     response code
     * @param fileName html file name in directory "resourceDirectory"
     */
    public HtmlResponse(final ResponseCodes code, final String fileName) {
        this(code, fileName, null);
    }

    /**
     * This constructor should only be used if you want to add html data separately and store it in a different file
     *
     * @param fileName html file name in directory "resourceDirectory"
     */
    public HtmlResponse(final String fileName) {
        this(null, fileName, null);
    }

    /**
     * This method looks for ejs notation and replaces it with data found in the map provided in the constructor
     * if it can't find a valid variable name it replaces the test with an upside down question mark
     *
     * @param data is the line that needs to be replaced
     * @return returns an updated line with different
     */
    private String customEjs(String data) {
        List<Integer> starting = getIndexes(data, "<%=");
        List<Integer> ending = getIndexes(data, "%>");
        if (starting.size() == ending.size()) {
            List<String> cutStrings = new LinkedList<>();
            List<String> vars = new LinkedList<>();
            for (int i = 0; i < starting.size(); i++) {
                String cutString = data.substring(starting.get(i), ending.get(i) + 2);
                String var = cutString.substring(cutString.indexOf("=") + 1, cutString.indexOf("%>")).trim();
                cutStrings.add(cutString);
                vars.add(var);
            }
            for (int i = 0; i < cutStrings.size(); i++) {
                if (vars.get(i).startsWith("list:")) {
                    String var = vars.get(i).substring(5);
                    if (map != null && map.get(var) != null) {
                        if (map.get(var) instanceof List) {
                            List<Object> list = (List<Object>) map.get(var);
                            StringBuilder builder = new StringBuilder("<br>");
                            for (Object a : list) {
                                builder.append(a.toString()).append("<br>");
                            }
                            data = data.replace(cutStrings.get(i), builder.toString());
                        }
                    }
                } else {
                    if (map != null && map.get(vars.get(i)) != null) {
                        data = data.replace(cutStrings.get(i), map.get(vars.get(i)).toString());
                    } else {
                        data = data.replaceAll(cutStrings.get(i), "¿");
                    }
                }
            }
        }
        return data;
    }

    /**
     * @param data   string to acquire data from
     * @param search string that needs to be search for
     * @return a list of indexes for parsing purposes
     */
    private List<Integer> getIndexes(final String data, final String search) {
        List<Integer> list = new LinkedList<>();
        int start = data.indexOf(search);
        while (start >= 0) {
            list.add(start);
            start = data.indexOf(search, start + 1);
        }
        return list;
    }

    /**
     * This method loops through the html file specified and stores all data into a stringbuilder
     * if it recognizes customEjs notation used throughout the file it will attempt to replace
     * it with data you provided in the map
     *
     * @return a compiled html response including header and html page
     */
    @Override
    public String getResponse() {
        if (header != null)
            builder.append(header.getHeader());
        try {
            if (file.exists() && file.canRead()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String data;
                while ((data = bufferedReader.readLine()) != null) {
                    if (data.contains("<%=") && data.contains("%>")) {
                        data = customEjs(data);
                    }
                    builder.append(data).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
