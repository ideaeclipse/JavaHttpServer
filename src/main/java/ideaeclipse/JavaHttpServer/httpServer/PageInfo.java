package ideaeclipse.JavaHttpServer.httpServer;

import ideaeclipse.JavaHttpServer.httpServer.responses.objects.RequestMethod;
import ideaeclipse.reflectionListener.parents.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to define directory / method info for an endpoint
 *
 * @author Myles T
 * @see ideaeclipse.reflectionListener.ListenerManager#callExecutablesByAnnotation(Event, Class, Object...)
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface PageInfo {
    RequestMethod method() default RequestMethod.GET;

    String directory() default "/";
}
