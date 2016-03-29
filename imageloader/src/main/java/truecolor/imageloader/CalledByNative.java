package truecolor.imageloader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiaowu on 15/7/15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface CalledByNative {
}
