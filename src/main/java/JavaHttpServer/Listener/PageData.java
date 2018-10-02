package JavaHttpServer.Listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom handler for {@link ideaeclipse.reflectionListener.EventManager}
 *
 * @author Ideaeclipse
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface PageData {
    enum Method {
        GET, POST, PUT, DELETE;
    }

    Method method() default Method.GET;

    String directory() default "/";
}
