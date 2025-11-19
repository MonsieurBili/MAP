package Service;

import Domain.Person.Persoana;
import Repository.IdGenerator;
import Repository.RepositoryPerson;

/**
 * Service class for managing Person entities.
 * Provides business logic operations for persons with automatic ID generation.
 */
public class ServicePerson extends ServiceEntity<Long, Persoana> {
    
    private final IdGenerator idGenerator;
    private final RepositoryPerson repository;
    
    /**
     * Constructs a ServicePerson with the specified ID generator and repository.
     *
     * @param idGenerator the ID generator for creating unique person IDs
     * @param repository  the repository for person data access
     */
    public ServicePerson(IdGenerator idGenerator, RepositoryPerson repository) {
        super(repository);
        this.idGenerator = idGenerator;
        this.repository = repository;
    }

    /**
     * Saves a new person with an automatically generated ID.
     *
     * @param persoana the person to save
     * @return the saved person with assigned ID
     */
    @Override
    public Persoana save(Persoana persoana) {
        persoana.setId(idGenerator.nextId());
        repository.save(persoana);
        return persoana;
    }
}
