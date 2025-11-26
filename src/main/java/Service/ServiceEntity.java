package Service;


import Domain.Entity;
import Repository.Repository;
import Repository.RepositoryEntity;

public abstract class ServiceEntity<ID, E extends Entity<ID>> implements Service<ID,E>
{
    private final Repository<ID,E> repository;

    public ServiceEntity(Repository<ID,E> repository)
    {
        this.repository = repository;
    }

    @Override
    public E findOne (ID id)
    {
        return repository.findOne(id);
    }

    @Override
    public Iterable<E> findAll()
    {
        return repository.findAll();
    }

    @Override
    public E save(E entity)
    {
        return repository.save(entity);
    }
    @Override
    public E update(E entity)
    {
        return repository.update(entity);
    }

    @Override
    public E delete(ID id)
    {
        return repository.delete(id);
    }
}
