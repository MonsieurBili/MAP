package Repository;

import Domain.Entity;
import Validators.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of the Repository interface using a HashMap.
 * Provides basic CRUD operations with validation support.
 *
 * @param <ID> the type of the entity identifier
 * @param <E>  the type of entities stored in this repository
 */
public class RepositoryEntity<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private final Validator<E> validator;
    private final Map<ID, E> entities;

    /**
     * Constructs a new RepositoryEntity with the specified validator.
     *
     * @param validator the validator to use for entity validation
     */
    public RepositoryEntity(Validator<E> validator) {
        this.validator = validator;
        this.entities = new HashMap<>();
    }

    @Override
    public E findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        validator.validate(entity);
        
        if (entities.containsKey(entity.getId())) {
            return entity; // Entity with this ID already exists
        }
        
        entities.put(entity.getId(), entity);
        return null; // Success
    }

    @Override
    public E delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        return entities.remove(id);
    }

    @Override
    public E update(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        validator.validate(entity);

        if (!entities.containsKey(entity.getId())) {
            return entity; // Entity does not exist
        }
        
        entities.put(entity.getId(), entity);
        return null; // Success
    }
}
