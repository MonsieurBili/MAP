package Service;

import Domain.Ducks.Duck;
import Domain.Person.Persoana;
import Repository.IdGenerator;
import Repository.Repository;
import Repository.RepositoryDuck;

public class ServiceDuck extends ServiceEntity<Long, Duck>{
    IdGenerator idGenerator;
    private Repository<Long, Duck> repository;

    public ServiceDuck(IdGenerator idGenerator, Repository<Long,Duck> repository) {
        super(repository);
        this.idGenerator = idGenerator;
        this.repository = repository;
    }
    @Override
    public Duck save(Duck duck)
    {
        repository.save(duck);
        return duck;
    }
}
