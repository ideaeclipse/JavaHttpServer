package JavaHttpServer;

import java.util.*;

/**
 * This class parses a url parameter string into a map of key's and values
 *
 * @author ideaeclipse
 */
public class Parameters {
    private final Hashtable<String, String> map;

    /**
     * @param parameters url string
     */
    Parameters(final String parameters) {
        ParameterParser parameterParser = new ParameterParser(parameters);
        this.map = parameterParser.getParameters();
    }

    /**
     * Blank object to avoid null's being thrown in {@link Server.Run#run()}
     */
    Parameters() {
        this.map = new Hashtable<>();
    }

    /**
     * @return map of parameters
     */
    public Hashtable<String, String> getMap() {
        return map;
    }

    /**
     * This class parses the string into a map
     */
    private class ParameterParser {
        private final Hashtable<String, String> map;
        private final String params;

        /**
         * if there is a '?' it is possible for the params to be parsed
         *
         * @param parameters url
         */
        private ParameterParser(final String parameters) {
            this.params = parameters;
            this.map = new Hashtable<>();
            String params = parameters.substring(parameters.indexOf('?') + 1, parameters.length());
            if (parameters.indexOf('?') > 0) {
                convert(params);
            }
        }

        /**
         * This method gets all the '&' which separate each different key and value pair in the url
         * after adding those indexes to a list you can substring it using those indexes to get individual key and value pairs
         *
         * @param params a parsed string from the ? onwards
         */
        private void convert(final String params) {
            Iterator<Integer> andSigns = getIndexesOf('&').iterator();
            int current, previous = 0;
            if (andSigns.hasNext()) {
                while (andSigns.hasNext()) {
                    current = andSigns.next() - 2;
                    split(map, params.substring(previous, current));
                    previous = current + 1;
                    if (!andSigns.hasNext()) {
                        split(map, params.substring(current + 1, params.length()));
                    }
                }
            } else {
                split(map, params);
            }
        }

        /**
         * @param c variable you want to split by
         * @return returns a list of all indexes that c is at
         */
        private List<Integer> getIndexesOf(final Character c) {
            int index = params.indexOf(c);
            List<Integer> temp = new LinkedList<>();
            while (index >= 0) {
                temp.add(index);
                index = params.indexOf(c, index + 1);
            }
            return temp;
        }

        /**
         * This method splits the key and value pairs into actual key and value variables
         *
         * @param table     table of data
         * @param splitable the potential splitable string
         */
        private void split(final Hashtable<String, String> table, final String splitable) {
            final int index = splitable.indexOf('=');
            table.put(splitable.substring(0, index), splitable.substring(index + 1, splitable.length()));
        }

        /**
         * @return finished table of data
         */
        Hashtable<String, String> getParameters() {
            return this.map;
        }
    }
}
