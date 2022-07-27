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
import nick1st.fancyvideo.api.eventbus.event.Event;
import nick1st.fancyvideo.api.eventbus.event.PlayerEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Eventbus all FancyVideo related events are fired on. Use {@link #getInstance()} to get access to the eventbus.<br>
 * Inspired by Keksuccino's Konkrete eventbus.
 *
 * @since 0.2.0.0
 */
@SuppressWarnings("unchecked")
public class FancyVideoEventBus {

    private static FancyVideoEventBus instance;

    private final Map<String, List<EventListener>> events = new HashMap<>();

    private FancyVideoEventBus() {
    }

    /**
     * @return The Eventbus all Events are fired on.
     * @since 0.2.0.0
     */
    public static synchronized FancyVideoEventBus getInstance() {
        if (FancyVideoEventBus.instance == null) {
            FancyVideoEventBus.instance = new FancyVideoEventBus();
        }
        return FancyVideoEventBus.instance;
    }

    /**
     * Registers all public (static and non-static) methods annotated with {@link FancyVideoEvent} to this Eventbus.
     * If the supplied Object is only a {@link Class}, then <b>only static</b> methods will be registered.
     *
     * @param o The object or class which methods should be registered.
     * @since 0.2.0.0
     */
    public void registerEvent(Object o) throws EventException.EventRegistryException, EventException.UnauthorizedRegistryException {
        boolean onlyStatic = o instanceof Class<?>; // If true, only get static members
        Class<?> callingClass = onlyStatic ? ((Class<?>) o) : o.getClass();
        Method[] declaredMethods = callingClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(FancyVideoEvent.class) && (Modifier.isStatic(method.getModifiers()) || !onlyStatic)) {
                EventPriority priority = method.getAnnotation(FancyVideoEvent.class).priority();
                if (priority == EventPriority.SURPREME && !(callingClass.getPackageName().startsWith("nick1st.fancyvideo"))) {
                    throw new EventException.UnauthorizedRegistryException("Tried to register methode '" + method.toGenericString()
                            + "' with EventPriority.SUPREME. This is only allowed for API classes!");
                } else {
                    if (method.getParameterCount() == 1) {
                        Class<?> identifier = method.getParameterTypes()[0];
                        if (Event.class.isAssignableFrom(identifier)) {
                            if (!listenersExist((Class<? extends Event>) identifier)) {
                                events.put(identifier.getName(), new ArrayList<>());
                            }
                            EventPhase phase = method.getAnnotation(FancyVideoEvent.class).phase();
                            DynamicResourceLocation player = method.getAnnotation(FancyVideoEvent.class).player().isBlank() ?
                                    Constants.EMPTY_RES_LOC :
                                    new DynamicResourceLocation(method.getAnnotation(FancyVideoEvent.class).player().split(":")[0], method.getAnnotation(FancyVideoEvent.class).player().split(":")[1]);
                            events.get(identifier.getName()).add(new EventListener(method, o, priority, phase, player));
                            Constants.LOG.debug("Registered event '{}' ({}) from '{}'!", method.getName(), identifier.getTypeName(), callingClass.getName());
                        } else {
                            throw new EventException.EventRegistryException("Methode '" + method.toGenericString() +
                                    "' is annotated with @FancyVideoEvent, and thus requires exactly ONE argument EXTENDING Event.");
                        }
                    } else {
                        throw new EventException.EventRegistryException("Methode '" + method.toGenericString() +
                                "' is annotated with @FancyVideoEvent, and thus requires exactly ONE argument EXTENDING Event.");
                    }
                }
            }
        }
    }

    /**
     * Needs to be called when you want an event to be executed.<br>
     * Always create a new Event to fire upon, don't reuse them. <br><br>
     *
     * <b>Usage:</b><br>
     * <p>
     * {@code ExampleEvent e = new ExampleEvent();}<br>
     * {@code FancyVideoEventBus.runEvent(e);}
     *
     * @since 0.2.0.0
     */
    public void runEvent(Event event) {
        if (event.hasPhases()) {
            fire(event, EventPhase.PRE);
        } else {
            fire(event, EventPhase.EVENT);
        }
    }

    /**
     * Mainly used for internal calls. Can be used if you want to only fire a special phase (Note: Phased events are normally
     * auto firing the next Phase after completion).<br>
     * Always create a new Event to fire upon, don't reuse them. <br><br>
     *
     * <b>Usage:</b><br>
     * <p>
     * {@code ExampleEvent e = new ExampleEvent();}<br>
     * {@code FancyVideoEventBus.fire(e, EventPhase.Event);}
     *
     * @since 0.2.0.0
     */
    public void fire(Event event, EventPhase phase) {
        if (listenersExist(event.getClass())) {
            List<EventListener> supreme = new ArrayList<>();
            List<EventListener> highest = new ArrayList<>();
            List<EventListener> high = new ArrayList<>();
            List<EventListener> normal = new ArrayList<>();
            List<EventListener> low = new ArrayList<>();
            List<EventListener> lowest = new ArrayList<>();
            for (EventListener c : events.get(event.getClass().getName())) {
                DynamicResourceLocation playerInQuestion = Constants.EMPTY_RES_LOC;
                if (event instanceof PlayerEvent) {
                    playerInQuestion = ((PlayerEvent) event).getPlayer();
                }
                if (playerInQuestion.equals(Constants.EMPTY_RES_LOC) || c.player().equals(Constants.EMPTY_RES_LOC) || playerInQuestion.equals(c.player())) {
                    if (c.phase() == phase) {
                        switch (c.priority()) {
                            case SURPREME -> supreme.add(c);
                            case HIGHEST -> highest.add(c);
                            case HIGH -> high.add(c);
                            case NORMAL -> normal.add(c);
                            case LOW -> low.add(c);
                            case LOWEST -> lowest.add(c);
                        }
                    }
                }
            }
            for (EventListener c : supreme) {
                this.invokeEvent(c, event);
            }
            if (event.isCanceled()) return;
            for (EventListener c : highest) {
                this.invokeEvent(c, event);
            }
            if (event.isCanceled()) return;
            for (EventListener c : high) {
                this.invokeEvent(c, event);
            }
            if (event.isCanceled()) return;
            for (EventListener c : normal) {
                this.invokeEvent(c, event);
            }
            if (event.isCanceled()) return;
            for (EventListener c : low) {
                this.invokeEvent(c, event);
            }
            if (event.isCanceled()) return;
            for (EventListener c : lowest) {
                this.invokeEvent(c, event);
            }
            event.onFinished();
        }
    }

    /**
     * Internal methode to check if there are already listeners for an event.
     *
     * @param event The event to check listeners for.
     * @return True if listeners exist, otherwise false.
     * @since 0.2.0.0
     */
    private boolean listenersExist(Class<? extends Event> event) {
        return (events.get(event.getName()) != null);
    }

    /**
     * Internal methode called to invoke an event.
     *
     * @param listener The listener to call.
     * @param event    The event to pass.
     * @since 0.2.0.0
     */
    private void invokeEvent(EventListener listener, Event event) {
        try {
            listener.event().invoke(listener.instance(), event);
        } catch (Exception e) {
            Constants.LOG.error("################# ERROR #################");
            Constants.LOG.error("Failed to invoke event!");
            Constants.LOG.error("Event Subscription Class: {}", listener.event().getDeclaringClass());
            Constants.LOG.error("Event Subscription Method Name: {}", listener.event().getName());
            Constants.LOG.error("Event Name: {}", event.getClass().getName());
            Constants.LOG.error("#########################################");
            Constants.LOG.error("");
            e.printStackTrace();
        }
    }
}
