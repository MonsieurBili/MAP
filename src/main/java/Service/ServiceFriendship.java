package Service;

import Domain.Friendship;
import Domain.Person.Persoana;
import Observer.Observable;
import ObserverGui.ObserverGui;
import ObserverGui.ObservableGui;
import Repository.FriendshipRepository;
import Repository.IdGenerator;
import Repository.Repository;
import util.EntityChangeEvent;
import util.EntityChangeEventType;

import java.util.ArrayList;
import java.util.List;

public class ServiceFriendship extends ServiceEntity<Long,Friendship> implements ObservableGui<EntityChangeEvent> {
    IdGenerator idGenerator;
    private Repository<Long, Friendship> repository;
    private List<ObserverGui<EntityChangeEvent>> observers = new ArrayList<>();

    public ServiceFriendship(IdGenerator idGenerator,Repository<Long,Friendship> friendshipRepository) {
        super(friendshipRepository);
        this.idGenerator = idGenerator;
        this.repository = friendshipRepository;
    }

    @Override
    public Friendship save(Friendship friendship) {
        friendship.setId(idGenerator.nextId());
        super.save(friendship);
        return friendship;
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
