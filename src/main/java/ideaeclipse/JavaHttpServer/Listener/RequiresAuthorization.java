package ideaeclipse.JavaHttpServer.Listener;

import ideaeclipse.reflectionListener.Listener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Allows the user to create a custom method to handle how their tokens get checked.
 * I.e. check db to ensure the user sent the right token
 */
public abstract class RequiresAuthorization {
    private final Listener listener;

    protected RequiresAuthorization(final Listener listener) {
        this.listener = listener;
    }

    /**
     * calls the handler to preform a token check
     *
     * @param dir requested directory
     * @param token submitted token
     * @return if the user is good to go
     */
    public boolean requiresAuth(final String dir, final String token) {
        boolean test = required(dir);
        if (test) {
            return handleToken(token);
        }
        return true;
    }

    /**
     * Checks whether the users requests requires an authorization tokeb
     *
     * @param dir   requested directory
     * @return status of authorization requirment
     */
    private boolean required(final String dir) {
        Method[] methods = listener.getClass().getDeclaredMethods();
        for (Method m : methods) {
            List<Annotation> list = Arrays.stream(m.getDeclaredAnnotations()).filter(o -> o.annotationType().equals(PageData.class)).collect(Collectors.toList());
            PageData annotation = (PageData) list.get(0);
            if (annotation.directory().equals(dir)) {
                List<Annotation> filtered = Arrays.stream(m.getDeclaredAnnotations()).filter(o -> o.annotationType().equals(Authorization.class)).collect(Collectors.toList());
                if (!filtered.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * token check
     * @param token token
     * @return if its the right token
     */
    public abstract Boolean handleToken(final String token);
}
