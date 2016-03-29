package truecolor.webdataloader.fastxml.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cris on 14-7-9.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface XMLType {
    /**
     * use separator to separate XML tag
     *
     * if # is separator
     * <root>
     *     <videos>
     *         <video>...</video>
     *     <videos/>
     * <root/>
     *
     * name "" for root element
     * name "videos" for tag videos
     * name "videos#video" for tag video
     */
    String name() default "";

    String separator() default "#";
}

