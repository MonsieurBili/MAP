package Service;

import Domain.Ducks.Duck;
import Repository.IdGenerator;
import Repository.Repository;

public class ServiceDuck extends ServiceEntity<Long, Duck>{
    IdGenerator idGenerator;
    Repository<Long, Duck> repository;

    public ServiceDuck(IdGenerator idGenerator, Repository<Long, Duck> repository) {
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
