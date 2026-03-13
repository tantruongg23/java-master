package exercises.service;

/**
 * Application service responsible for user lifecycle operations.
 *
 * <p>Depends on:
 * <ul>
 *   <li>{@link UserRepository} — persistence</li>
 *   <li>{@link EmailService} — notifications</li>
 *   <li>{@link AuditLogger} — audit trail</li>
 * </ul>
 *
 * <p><strong>Exercise 2:</strong> Test this class by mocking all dependencies.
 */
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuditLogger auditLogger;

    public UserService(UserRepository userRepository,
                       EmailService emailService,
                       AuditLogger auditLogger) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.auditLogger = auditLogger;
    }

    /**
     * Registers a new user.
     *
     * <p>Happy path: save user, send welcome email, log the action.
     *
     * @param request the registration data
     * @throws DuplicateEmailException if the email is already registered
     * @throws IllegalArgumentException if request data is invalid
     */
    public User register(UserRequest request) {
        // TODO: Validate request (non-null, non-blank fields)
        // TODO: Check for duplicate email via repository
        // TODO: Save user
        // TODO: Send welcome email
        // TODO: Log the registration action
        throw new UnsupportedOperationException("TODO: implement register");
    }

    /**
     * Initiates a password reset for the given email address.
     *
     * @param email the user's email
     * @throws UserNotFoundException if no user with that email exists
     */
    public void resetPassword(String email) {
        // TODO: Find user by email
        // TODO: Generate reset token
        // TODO: Send password-reset email with token
        // TODO: Log the reset action
        throw new UnsupportedOperationException("TODO: implement resetPassword");
    }

    /**
     * Deactivates a user account.
     *
     * @param userId the user's identifier
     * @throws UserNotFoundException if the user does not exist
     */
    public void deactivate(String userId) {
        // TODO: Find user by id
        // TODO: Mark user as inactive
        // TODO: Save updated user
        // TODO: Send farewell email
        // TODO: Log the deactivation action
        throw new UnsupportedOperationException("TODO: implement deactivate");
    }

    // ---- Supporting types (nested for simplicity) ----

    public record UserRequest(String name, String email, String password) {}

    public record User(String id, String name, String email, boolean active) {}

    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String email) {
            super("Email already registered: " + email);
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String identifier) {
            super("User not found: " + identifier);
        }
    }
}
