package exercises.service;

import exercises.service.UserService.DuplicateEmailException;
import exercises.service.UserService.User;
import exercises.service.UserService.UserNotFoundException;
import exercises.service.UserService.UserRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * Mock-based test suite for {@link UserService}.
 *
 * <p>Exercise 2 — use BDDMockito style throughout.
 * Verify all interactions, capture arguments where needed.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<String> emailBodyCaptor;

    // ------------------------------------------------------------------
    // Registration
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("register()")
    class RegisterTests {

        private final UserRequest validRequest =
                new UserRequest("Alice", "alice@example.com", "Str0ngP@ss!");

        @Test
        @DisplayName("should save user, send welcome email, and log the action")
        void happyPath() {
            // TODO: given — repository returns no existing user, save returns a user
            // TODO: when — call register
            // TODO: then — verify save, sendEmail, and log were called
        }

        @Test
        @DisplayName("should throw DuplicateEmailException when email already exists")
        void duplicateEmail() {
            // TODO: given — repository.findByEmail returns a user
            // TODO: when/then — assertThrows(DuplicateEmailException.class, ...)
            // TODO: verify save was never called
        }

        @Test
        @DisplayName("should throw when request has blank name")
        void invalidName() {
            // TODO: create request with blank name
            // TODO: assertThrows(IllegalArgumentException.class, ...)
        }

        @Test
        @DisplayName("should throw when request has blank email")
        void invalidEmail() {
            // TODO: create request with blank email
            // TODO: assertThrows(IllegalArgumentException.class, ...)
        }

        @Test
        @DisplayName("should include user name in welcome email body")
        void welcomeEmailContent() {
            // TODO: given — setup mocks
            // TODO: when — register
            // TODO: then — capture email body, assert it contains "Alice"
        }
    }

    // ------------------------------------------------------------------
    // Password Reset
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("resetPassword()")
    class ResetPasswordTests {

        @Test
        @DisplayName("should send reset email when user exists")
        void happyPath() {
            // TODO: given — repository.findByEmail returns a user
            // TODO: when — call resetPassword
            // TODO: then — verify email sent with subject containing "reset"
        }

        @Test
        @DisplayName("should throw UserNotFoundException for unknown email")
        void unknownEmail() {
            // TODO: given — repository.findByEmail returns empty
            // TODO: when/then — assertThrows(UserNotFoundException.class, ...)
        }

        @Test
        @DisplayName("should log the password reset action")
        void auditLogged() {
            // TODO: verify auditLogger.log was called with "RESET_PASSWORD"
        }
    }

    // ------------------------------------------------------------------
    // Deactivation
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("deactivate()")
    class DeactivateTests {

        @Test
        @DisplayName("should mark user inactive, send farewell email, and log")
        void happyPath() {
            // TODO: given — repository.findById returns active user
            // TODO: when — deactivate
            // TODO: then — verify save with inactive user, email sent, log called
        }

        @Test
        @DisplayName("should throw UserNotFoundException for non-existent user")
        void unknownUser() {
            // TODO: given — repository.findById returns empty
            // TODO: when/then — assertThrows(UserNotFoundException.class, ...)
        }

        @Test
        @DisplayName("should send farewell email before logging")
        void interactionOrder() {
            // TODO: use InOrder to verify email is sent before audit log
        }
    }
}
