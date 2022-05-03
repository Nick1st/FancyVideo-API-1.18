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

package nick1st.fancyvideo.api.eventbus.event; //NOSONAR

import nick1st.fancyvideo.api.eventbus.EventPhase;
import nick1st.fancyvideo.api.eventbus.FancyVideoEventBus;

/**
 * Base class of all phased events, extend this if you want an event with phases.
 *
 * @see Event
 * @since 0.2.0.0
 */
public abstract class PhasedEvent extends Event {

    protected EventPhase phase = EventPhase.PRE;

    /**
     * @return True, as long as the phase is not {@link EventPhase}.Post;
     * @since 0.2.0.0
     */
    @Override
    public boolean isCancelable() {
        return phase != EventPhase.POST;
    }

    /**
     * @return True
     * @since 0.2.0.0
     */
    @Override
    public boolean hasPhases() {
        return true;
    }

    /**
     * Will call the next phase, as soon as the previous one finished.
     * Override this methode to override this behavior. For other behavior changes use {@link #afterPhase()}.
     *
     * @since 0.2.0.0
     */
    @Override
    public void onFinished() {
        afterPhase();
        if (!isCanceled() && phase != EventPhase.POST) {
            phase = EventPhase.values()[phase.ordinal()];
            FancyVideoEventBus.getInstance().fire(this, phase);
        }
    }

    /**
     * Template methode. <br>
     * Meant for simple behavior changes after a phase.
     *
     * @see #onFinished()
     * @since 0.2.0.0
     */
    public abstract void afterPhase();
}
