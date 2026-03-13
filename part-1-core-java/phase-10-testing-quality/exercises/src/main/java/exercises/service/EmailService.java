package exercises.service;

/**
 * Port for sending email notifications.
 *
 * <p>In tests this interface is mocked to verify that the correct
 * emails are sent with the expected content.
 */
public interface EmailService {

    /**
     * Sends an email.
     *
     * @param to      recipient address
     * @param subject email subject line
     * @param body    email body (plain text or HTML)
     */
    void sendEmail(String to, String subject, String body);
}
