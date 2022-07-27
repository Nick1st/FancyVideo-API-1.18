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

import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.DynamicResourceLocation;

import java.lang.reflect.Method;

/**
 * @param event    The methode to send to.
 * @param instance The object or class to send to.
 * @param priority The priority of this event.
 * @param phase    The phase we should send to the methode.
 * @param player   The player the event should fire for.
 * @see EventPriority
 * @see nick1st.fancyvideo.api.eventbus.event.Event
 * @since 2.2.0.4
 */
public record EventListener(Method event, Object instance, EventPriority priority, EventPhase phase, DynamicResourceLocation player) {

    /**
     * @param event    The methode to send to.
     * @param instance The object or class to send to.
     * @param priority The priority of this event.
     * @param phase    The phase we should send to the methode.
     * @see EventPriority
     * @see nick1st.fancyvideo.api.eventbus.event.Event
     * @since 0.2.0.0
     * @deprecated Use the main constructor instead
     */
    @Deprecated(since = "2.2.0.4")
    public EventListener(Method event, Object instance, EventPriority priority, EventPhase phase) {
        this(event, instance, priority, phase, Constants.EMPTY_RES_LOC);
    }
}
