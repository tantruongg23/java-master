package exercises.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Repository — a generic, type-safe data access interface.
 *
 * <p>Defines standard CRUD operations and predicate-based querying.
 *
 * <p>Type parameters:
 * <ul>
 *   <li>{@code T} — the entity type, which must be {@link Identifiable}</li>
 *   <li>{@code ID} — the type of the entity's unique identifier</li>
 * </ul>
 *
 * @param <T>  the entity type
 * @param <ID> the identifier type
 */
public interface Repository<T extends Identifiable<ID>, ID> {

    /**
     * Find an entity by its unique identifier.
     *
     * @param id the identifier
     * @return an {@link Optional} containing the entity, or empty if not found
     */
    Optional<T> findById(ID id);

    /**
     * Return all entities in the repository.
     *
     * @return an unmodifiable list of all entities
     */
    List<T> findAll();

    /**
     * Save (insert or update) an entity.
     *
     * <p>If an entity with the same ID already exists, it is replaced.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);

    /**
     * Delete an entity by its identifier.
     *
     * @param id the identifier of the entity to delete
     * @return {@code true} if the entity was found and deleted
     */
    boolean delete(ID id);

    /**
     * Find all entities matching the given predicate.
     *
     * @param filter the predicate to test each entity against
     * @return a list of matching entities
     */
    List<T> findBy(Predicate<T> filter);
}
