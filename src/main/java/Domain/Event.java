package Domain;

import Observer.Observable;
import Observer.Observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Event<T extends Observer> extends Entity<Long> implements Observable<T> {
    private String name;
    private String location;
    protected List<T> participants;

    Event(String name,String location) {
        this.name = name;
        this.location = location;
        participants = new ArrayList<>();
    }
    @Override
    public void addObserver(T o) {
        participants.add(o);
    }

    @Override
    public void removeObserver(T o) {
        participants.remove(o);
    }

    @Override
    public void notifyObservers() {
        participants.forEach(o -> o.update());
    }

    public List<T>  getParticipants() {
        return participants;
    }

    public String getName()
    {
        return name;
    }

    public String getLocation()
    {
        return location;
    }
    public abstract void subscribe(User o);
    public abstract void unsubscribe(User o);

}
