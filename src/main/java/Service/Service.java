package Service;

import Domain.Entity;

/**
 * Generic service interface defining business logic operations for entities.
 * This interface provides a layer of abstraction between the UI and repository layers.
 *
 * @param <ID> the type of the entity identifier
 * @param <E>  the type of entities managed by this service
 */
public interface Service<ID, E extends Entity<ID>> {

    /**
     * Finds and returns an entity by its identifier.
     *
     * @param id the identifier of the entity to find
     * @return the entity with the specified id, or null if not found
     */
    E findOne(ID id);

    /**
     * Returns all entities managed by this service.
     *
     * @return an Iterable containing all entities
     */
    Iterable<E> findAll();

    /**
     * Saves a new entity.
     *
     * @param entity the entity to save
     * @return the saved entity with assigned ID
     */
    E save(E entity);

    /**
     * Updates an existing entity.
     *
     * @param entity the entity with updated data
     * @return the updated entity, or null if the entity doesn't exist
     */
    E update(E entity);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id the identifier of the entity to delete
     * @return the deleted entity, or null if the entity doesn't exist
     */
    E delete(ID id);
}
