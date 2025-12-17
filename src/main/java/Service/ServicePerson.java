package Service;


import Domain.Person.Persoana;
import ObserverGui.ObservableGui;
import ObserverGui.ObserverGui;
import Repository.Database.RepositoryPersonDB;
import Repository.IdGenerator;
import Repository.Repository;
import Repository.RepositoryPerson;
import util.EntityChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class ServicePerson extends ServiceEntity<Long, Persoana> implements ObservableGui<EntityChangeEvent> {
    IdGenerator idGenerator;
    private Repository<Long,Persoana> repository;
    private List<ObserverGui<EntityChangeEvent>> observers = new ArrayList<>();

    public ServicePerson(IdGenerator idGenerator,Repository<Long,Persoana> repository) {
        super(repository);
        this.idGenerator = idGenerator;
        this.repository = repository;
    }

    @Override
    public Persoana save(Persoana persoana)
    {
        repository.save(persoana);
        return persoana;
    }


    @Override
    public void addObserver(ObserverGui<EntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void notifyObservers(EntityChangeEvent e) {
        observers.stream().forEach(o -> o.update(e));
    }

    @Override
    public void removeObserver(ObserverGui<EntityChangeEvent> e) {
        observers.remove(e);
    }
}
