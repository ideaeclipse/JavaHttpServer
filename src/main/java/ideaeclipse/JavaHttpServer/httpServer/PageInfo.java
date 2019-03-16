package ideaeclipse.JavaHttpServer.httpServer;

import ideaeclipse.JavaHttpServer.httpServer.responses.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface PageInfo {
    RequestMethod method() default RequestMethod.GET;

    String directory() default "/";
}
