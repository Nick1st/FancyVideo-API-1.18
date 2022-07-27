/*
 * This file is part of the FancyVideo-API.
 *
 * The FancyVideo-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The FancyVideo-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * The FancyVideo-API uses VLCJ, Copyright 2009-2021 Caprica Software Limited,
 * licensed under the GNU General Public License.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You should have received a copy of the GNU General Public License
 * along with FancyVideo-API.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2022 Nick1st.
 */

package nick1st.fancyvideo.api.eventbus; //NOSONAR

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A methode annotated with this will be considered a valid event listener.
 *
 * @see EventPhase
 * @see EventPriority
 * @since 0.2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FancyVideoEvent {

    EventPriority priority() default EventPriority.NORMAL;

    EventPhase phase() default EventPhase.EVENT;

    /**
     * This specifies the player an event should run for.
     * Note that this does <b>NOT</b> stop others from catching the event.
     *
     * @since 2.2.0.4
     */
    String player() default "";
}
