package Repository;

import Domain.Entity;

/**
 * Generic repository interface defining CRUD operations for entities.
 * This interface follows the Repository pattern for data access abstraction.
 *
 * @param <ID> the type of the entity identifier - must not be null
 * @param <E>  the type of entities saved in repository - must extend Entity<ID>
 */
public interface Repository<ID, E extends Entity<ID>> {
    
    /**
     * Finds and returns an entity by its identifier.
     *
     * @param id the id of the entity to be returned; must not be null
     * @return the entity with the specified id, or null if no entity exists with the given id
     * @throws IllegalArgumentException if id is null
     */
    E findOne(ID id);

    /**
     * Returns all entities in the repository.
     *
     * @return an Iterable containing all entities
     */
    Iterable<E> findAll();

    /**
     * Saves the given entity to the repository.
     * If an entity with the same ID already exists, the save operation fails.
     *
     * @param entity the entity to save; must not be null
     * @return null if the given entity is saved successfully,
     *         otherwise returns the entity (indicating that the id already exists)
     * @throws IllegalArgumentException if the given entity is null
     * @throws Exception.ValidationException if the entity is not valid
     */
    E save(E entity);

    /**
     * Removes the entity with the specified id from the repository.
     *
     * @param id the id of the entity to remove; must not be null
     * @return the removed entity, or null if there is no entity with the given id
     * @throws IllegalArgumentException if the given id is null
     */
    E delete(ID id);

    /**
     * Updates the given entity in the repository.
     * The entity is identified by its ID.
     *
     * @param entity the entity with updated data; must not be null
     * @return null if the entity is updated successfully,
     *         otherwise returns the entity (indicating that the id does not exist)
     * @throws IllegalArgumentException if the given entity is null
     * @throws Exception.ValidationException if the entity is not valid
     */
    E update(E entity);
}


