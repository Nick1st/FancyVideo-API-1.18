package nick1st.fancyvideo.api.helpers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks methods in MediaPlayer classes that can be called during a search query for a compatible media
 * player.
 * @since 3.0.0
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface CalledByQuery {
}
