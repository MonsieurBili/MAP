package Service;


import Domain.Person.Persoana;
import Repository.IdGenerator;
import Repository.RepositoryPerson;

public class ServicePerson extends ServiceEntity<Long, Persoana> {
    IdGenerator idGenerator;
    RepositoryPerson repository;
    public ServicePerson(IdGenerator idGenerator,RepositoryPerson repository) {
        super(repository);
        this.idGenerator = idGenerator;
        this.repository = repository;
    }

    @Override
    public Persoana save(Persoana persoana)
    {
        persoana.setId(idGenerator.nextId());
        repository.save(persoana);
        return persoana;
    }


}
