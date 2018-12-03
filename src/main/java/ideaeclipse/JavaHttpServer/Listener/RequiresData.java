package ideaeclipse.JavaHttpServer.Listener;

import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.reflectionListener.Listener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Processes a method to see if it requires certain data to be passed.
 * @author Ideaeclipse
 */
public class RequiresData {
    private final Listener listener;
    private String requires;

    public RequiresData(final Listener listener) {
        this.listener = listener;
    }

    /**
     * @param json data passed
     * @param dir directory that is requested
     * @return if true everything is in order, else you can call getRequires to get a string of all the missing params
     */
    public boolean requiresData(final String json, String dir) {
        String requires = requires(json, dir);
        if (Boolean.parseBoolean(requires))
            return Boolean.parseBoolean(requires);
        else
            this.requires = requires;
        return false;
    }

    /**
     * @param string json string passed from request
     * @param dir directory being requested from
     * @return string {true,false, missing params array}
     */
    private String requires(final String string, String dir) {
        Method[] methods = listener.getClass().getDeclaredMethods();
        for (Method m : methods) {
            List<Annotation> list = Arrays.stream(m.getDeclaredAnnotations()).filter(o -> o.annotationType().equals(PageData.class)).collect(Collectors.toList());
            PageData annotation = (PageData) list.get(0);
            if (annotation.directory().equals(dir)) {
                List<Annotation> filtered = Arrays.stream(m.getDeclaredAnnotations()).filter(o -> o.annotationType().equals(Data.class)).collect(Collectors.toList());
                if (!filtered.isEmpty()) {
                    Data data = (Data) filtered.get(0);
                    if (string == null)
                        return Arrays.toString(data.values());
                    Json json = new Json(string);
                    List<String> remaining = new LinkedList<>();
                    for (String s : data.values())
                        if (!json.keySet().contains(s))
                            remaining.add(s);
                    if (!remaining.isEmpty())
                        return String.valueOf(remaining);
                }
            }
        }
        return String.valueOf(true);
    }

    /**
     * Potentially null.
     * @return Returns missing params if any
     */
    public String getRequires() {
        return requires;
    }
}
