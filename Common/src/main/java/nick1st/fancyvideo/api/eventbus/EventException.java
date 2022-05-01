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

import nick1st.fancyvideo.api.eventbus.event.Event;

import java.io.Serial;

/**
 * Base class of all default EventSystem Exceptions.
 *
 * @since 0.2.0.0
 */
public class EventException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public EventException(String msg) {
        super(msg);
    }

    /**
     * Thrown upon problems at event registration.
     *
     * @see FancyVideoEventBus#registerEvent(Object)
     * @since 0.2.0.0
     */
    public static class EventRegistryException extends EventException {
        public EventRegistryException(String msg) {
            super(msg);
        }
    }

    /**
     * Thrown when trying to register a SUPREME-Priority.
     *
     * @since 0.2.0.0
     */
    public static class UnauthorizedRegistryException extends EventException {
        public UnauthorizedRegistryException(String msg) {
            super(msg);
        }
    }

    /**
     * Thrown when trying to cancel a non-cancelable event.
     *
     * @see Event#isCanceled()
     * @since 0.2.0.0
     */
    public static class EventCancellationException extends EventException {
        public EventCancellationException(String msg) {
            super(msg);
        }
    }
}
