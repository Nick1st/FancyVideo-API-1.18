package nick1st.fancyvideo.api.player.querys;

import java.util.function.Predicate;

/**
 * A set of the most basic logic operations as queries.
 * See also: {@link Predicate#negate()}
 * @since 3.0.0
 */
public class LogicQueries {

    /**
     * All queries will be joined by an and.
     * @param queries The queries to join
     * @return A function evaluating the result
     * @since 3.0.0
     */
    public static Query And(Query... queries) {
        return mediaPlayer -> {
            for (Query query: queries) {
                if (!query.test(mediaPlayer)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * All queries will be joined by an or.
     * @param queries The queries to join
     * @return A function evaluating the result
     * @since 3.0.0
     */
    public static Query Or(Query... queries) {
        return mediaPlayer -> {
            for (Query query: queries) {
                if (query.test(mediaPlayer)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * The queries will be joined by an exclusive or.
     * @param query1 The first query to join
     * @param query2 The second query to join
     * @return A function evaluating the result
     * @since 3.0.0
     */
    public static Query XOr(Query query1, Query query2) {
        return mediaPlayer -> query1.test(mediaPlayer) ^ query2.test(mediaPlayer);
    }
}
