package Service;


import Domain.Person.Persoana;
import Repository.Database.RepositoryPersonDB;
import Repository.IdGenerator;
import Repository.Repository;
import Repository.RepositoryPerson;

public class ServicePerson extends ServiceEntity<Long, Persoana> {
    IdGenerator idGenerator;
    private Repository<Long,Persoana> repository;
    public ServicePerson(IdGenerator idGenerator,Repository<Long,Persoana> repository) {
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
