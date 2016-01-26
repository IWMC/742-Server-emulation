package sh.iwmc.core.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Brent on 01/24/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceManifest {

    String name();
    ThreadingType threadingType() default ThreadingType.SYNCHRONOUS;

    enum ThreadingType {
        SYNCHRONOUS, ASYNCHRONOUS;
    }
}
