package exercises.service;

/**
 * Port for recording audit events.
 *
 * <p>Implementations may write to a file, database, or external service.
 * In tests this interface is mocked to verify that actions are logged.
 */
public interface AuditLogger {

    /**
     * Logs an action performed on a user.
     *
     * @param action  short description of the action (e.g. "REGISTER", "DEACTIVATE")
     * @param userId  the affected user's identifier
     * @param details additional context
     */
    void log(String action, String userId, String details);
}
