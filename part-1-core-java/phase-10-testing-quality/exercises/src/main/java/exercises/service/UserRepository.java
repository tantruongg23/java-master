package exercises.service;

import java.util.Optional;

/**
 * Data access port for {@link exercises.service.UserService.User} entities.
 *
 * <p>Implementations may use JDBC, JPA, or an in-memory map.
 * In tests this interface is mocked.
 */
public interface UserRepository {

    /**
     * Finds a user by email address.
     *
     * @param email the email to search for
     * @return the user, or empty if not found
     */
    Optional<UserService.User> findByEmail(String email);

    /**
     * Finds a user by unique identifier.
     *
     * @param id the user id
     * @return the user, or empty if not found
     */
    Optional<UserService.User> findById(String id);

    /**
     * Persists a user (insert or update).
     *
     * @param user the user to save
     * @return the saved user (may contain a generated id)
     */
    UserService.User save(UserService.User user);

    /**
     * Permanently removes a user.
     *
     * @param id the user id to delete
     */
    void delete(String id);
}
