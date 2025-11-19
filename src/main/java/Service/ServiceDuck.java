package Service;

import Domain.Ducks.Duck;
import Repository.IdGenerator;
import Repository.RepositoryDuck;

public class ServiceDuck extends ServiceEntity<Long, Duck>{
    IdGenerator idGenerator;
    RepositoryDuck repository;

    public ServiceDuck(IdGenerator idGenerator, RepositoryDuck repository) {
        super(repository);
        this.idGenerator = idGenerator;
        this.repository = repository;
    }
    @Override
    public Duck save(Duck duck)
    {
        duck.setId(idGenerator.nextId());
        repository.save(duck);
        return duck;
    }
}
