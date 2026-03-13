package exercises.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * InMemoryRepository — a HashMap-backed implementation of {@link Repository}.
 *
 * <p>Demonstrates bounded generics: {@code T extends Identifiable<ID>} ensures
 * that every entity has a {@code getId()} method for key-based storage.
 *
 * @param <T>  the entity type
 * @param <ID> the identifier type
 */
public class InMemoryRepository<T extends Identifiable<ID>, ID> implements Repository<T, ID> {

    private final Map<ID, T> store = new HashMap<>();

    @Override
    public Optional<T> findById(ID id) {
        // TODO: Look up the entity in the store map.
        //       Return Optional.ofNullable(store.get(id)).

        throw new UnsupportedOperationException("TODO: implement findById()");
    }

    @Override
    public List<T> findAll() {
        // TODO: Return an unmodifiable list of all values in the store.
        //       Use List.copyOf(store.values()).

        throw new UnsupportedOperationException("TODO: implement findAll()");
    }

    @Override
    public T save(T entity) {
        // TODO: 1. Validate entity is not null.
        //       2. Put entity into the store using entity.getId() as the key.
        //       3. Return the saved entity.

        throw new UnsupportedOperationException("TODO: implement save()");
    }

    @Override
    public boolean delete(ID id) {
        // TODO: Remove the entity with the given id.
        //       Return true if it was present, false otherwise.

        throw new UnsupportedOperationException("TODO: implement delete()");
    }

    @Override
    public List<T> findBy(Predicate<T> filter) {
        // TODO: Stream over all values, filter by the predicate, collect to a list.
        //       Or iterate manually and build a list.

        throw new UnsupportedOperationException("TODO: implement findBy()");
    }

    /**
     * @return the number of entities in the repository
     */
    public int count() {
        return store.size();
    }

    // BONUS TODO: Add pagination support.
    //   public Page<T> findAll(int pageNumber, int pageSize) { ... }
    //
    //   Consider creating a Page<T> record:
    //     record Page<T>(List<T> content, int pageNumber, int pageSize, long totalElements) {}
}
