package exercises.repository;

/**
 * Identifiable — marks an entity as having a unique identifier.
 *
 * <p>Used as a type bound for the {@link Repository} interface to ensure
 * that all stored entities can be identified and looked up.
 *
 * @param <ID> the type of the unique identifier
 */
public interface Identifiable<ID> {

    /**
     * @return the unique identifier of this entity
     */
    ID getId();
}
