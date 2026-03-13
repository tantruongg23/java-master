package exercises.notification;

import java.util.*;

/**
 * Central notification service implementing the <b>Observer</b> pattern.
 *
 * <p>Subscribers register interest in specific event types. When an event is
 * fired the service delivers a {@link Notification} to every subscriber via
 * the appropriate {@link DeliveryChannel} (Strategy pattern).</p>
 *
 * <h3>Design patterns used</h3>
 * <ul>
 *   <li><b>Observer</b> — subscribe / unsubscribe / notify</li>
 *   <li><b>Strategy</b> — pluggable {@link DeliveryChannel} implementations</li>
 * </ul>
 *
 * @see Notification
 * @see DeliveryChannel
 */
public class NotificationService {

    /**
     * Strategy interface for delivering notifications.
     * Implement this for each channel: Email, SMS, Push, Slack.
     */
    public interface DeliveryChannel {
        /**
         * Deliver the given notification to its recipient.
         *
         * @param notification the notification to deliver
         */
        void deliver(Notification notification);
    }

    /**
     * Listener that is called when a subscribed event fires.
     */
    @FunctionalInterface
    public interface EventListener {
        void onEvent(Notification notification);
    }

    // TODO: Data structure to hold event-type -> list of listeners.
    //       Consider using Map<String, List<EventListener>>.
    private final Map<String, List<EventListener>> listeners = new HashMap<>();

    // TODO: Registry of available delivery channels keyed by channel name.
    private final Map<String, DeliveryChannel> channels = new HashMap<>();

    /**
     * Register a delivery channel under the given name.
     *
     * @param name    channel identifier (e.g. "email", "sms")
     * @param channel the delivery strategy
     */
    public void registerChannel(String name, DeliveryChannel channel) {
        channels.put(name, channel);
    }

    /**
     * Subscribe a listener to a specific event type.
     *
     * @param eventType the event type to listen for (e.g. "order.shipped")
     * @param listener  callback to invoke when the event fires
     */
    public void subscribe(String eventType, EventListener listener) {
        // TODO: Add the listener to the map entry for eventType.
        //       Create the list if it doesn't exist yet.
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Remove a previously registered listener.
     *
     * @param eventType the event type the listener is registered under
     * @param listener  the listener to remove
     */
    public void unsubscribe(String eventType, EventListener listener) {
        // TODO: Remove the listener from the list for the given event type.
        List<EventListener> subs = listeners.get(eventType);
        if (subs != null) {
            subs.remove(listener);
        }
    }

    /**
     * Fire an event: build a {@link Notification} and deliver it to every
     * subscriber of the given event type.
     *
     * @param eventType    the event type being fired
     * @param notification the notification to deliver
     */
    public void notify(String eventType, Notification notification) {
        // TODO: 1. Look up all listeners for the eventType.
        //       2. For each listener, invoke onEvent(notification).
        //       3. Look up the DeliveryChannel for notification.getChannel()
        //          and call deliver(notification).
        //
        // Bonus: deliver asynchronously using CompletableFuture.
        throw new UnsupportedOperationException("TODO: implement notify");
    }
}
