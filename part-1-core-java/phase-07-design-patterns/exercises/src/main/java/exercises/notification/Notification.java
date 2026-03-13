package exercises.notification;

/**
 * Immutable notification value object built using the <b>Builder</b> pattern.
 *
 * <p>Solves the telescoping-constructor problem: instead of multiple overloaded
 * constructors, callers use a fluent API:</p>
 *
 * <pre>{@code
 * Notification n = Notification.builder()
 *     .recipient("alice@example.com")
 *     .message("Your order has shipped!")
 *     .type("order.shipped")
 *     .urgency(Urgency.HIGH)
 *     .channel("email")
 *     .build();
 * }</pre>
 *
 * <h3>Design pattern: Builder</h3>
 */
public class Notification {

    /** Urgency level of the notification. */
    public enum Urgency {
        LOW, NORMAL, HIGH, CRITICAL
    }

    private final String recipient;
    private final String message;
    private final String type;
    private final Urgency urgency;
    private final String channel;

    private Notification(Builder builder) {
        this.recipient = builder.recipient;
        this.message = builder.message;
        this.type = builder.type;
        this.urgency = builder.urgency;
        this.channel = builder.channel;
    }

    public String getRecipient() { return recipient; }
    public String getMessage()   { return message; }
    public String getType()      { return type; }
    public Urgency getUrgency()  { return urgency; }
    public String getChannel()   { return channel; }

    @Override
    public String toString() {
        return "Notification{" +
                "recipient='" + recipient + '\'' +
                ", type='" + type + '\'' +
                ", urgency=" + urgency +
                ", channel='" + channel + '\'' +
                '}';
    }

    /** Entry point for the fluent builder. */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for {@link Notification}.
     *
     * <p>TODO:</p>
     * <ul>
     *   <li>Add validation in {@link #build()} — recipient and message are required.</li>
     *   <li>Provide sensible defaults for urgency ({@code NORMAL}) and channel ({@code "email"}).</li>
     *   <li>Consider adding a {@code copy()} method that pre-populates the builder from an
     *       existing Notification (Prototype-like convenience).</li>
     * </ul>
     */
    public static class Builder {

        private String recipient;
        private String message;
        private String type;
        private Urgency urgency = Urgency.NORMAL;
        private String channel = "email";

        private Builder() {}

        public Builder recipient(String recipient) {
            this.recipient = recipient;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder urgency(Urgency urgency) {
            this.urgency = urgency;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        /**
         * Build the immutable {@link Notification}.
         *
         * @return a new Notification instance
         * @throws IllegalStateException if required fields are missing
         */
        public Notification build() {
            // TODO: Validate that recipient and message are non-null/non-blank.
            //       Throw IllegalStateException with a descriptive message if not.
            return new Notification(this);
        }
    }
}
