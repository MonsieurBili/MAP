package Service;

import Domain.Ducks.Duck;
import Domain.Person.Persoana;
import ObserverGui.ObservableGui;
import ObserverGui.ObserverGui;
import Repository.Database.RepositoryDuckDB;
import Repository.IdGenerator;
import Repository.Repository;
import Repository.RepositoryDuck;
import util.EntityChangeEvent;
import util.paging.Page;
import util.paging.Pageable;

import java.util.ArrayList;
import java.util.List;

public class ServiceDuck extends ServiceEntity<Long, Duck> implements ObservableGui<EntityChangeEvent> {
    IdGenerator idGenerator;
    private RepositoryDuckDB repository;
    private List<ObserverGui<EntityChangeEvent>> observers = new ArrayList<>();


    public ServiceDuck(IdGenerator idGenerator, RepositoryDuckDB repository) {
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
    public Page<Duck> findAllOnPage(Pageable pageable) {
        return repository.findAllOnPage(pageable);
    }

    public Page<Duck> findAllOnPageFiltered(Pageable pageable, String typeFilter) {
        return repository.findAllOnPageFiltered(pageable, typeFilter);
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
