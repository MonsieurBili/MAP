package Domain;

import Observer.Observable;
import Observer.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for events in the system.
 * Implements the Observable pattern to notify participants of event changes.
 *
 * @param <T> the type of observers (participants) for this event
 */
public abstract class Event<T extends Observer> extends Entity<Long> implements Observable<T> {
    
    private String name;
    private String location;
    protected List<T> participants;

    /**
     * Constructs a new Event with the specified name and location.
     *
     * @param name     the name of the event
     * @param location the location where the event takes place
     */
    Event(String name, String location) {
        this.name = name;
        this.location = location;
        this.participants = new ArrayList<>();
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
        participants.forEach(Observer::update);
    }

    /**
     * Gets the list of participants in this event.
     *
     * @return the list of participants
     */
    public List<T> getParticipants() {
        return participants;
    }

    /**
     * Gets the name of this event.
     *
     * @return the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the location of this event.
     *
     * @return the event location
     */
    public String getLocation() {
        return location;
    }
    
    /**
     * Subscribes a user to receive notifications about this event.
     *
     * @param o the user to subscribe
     */
    public abstract void subscribe(User o);
    
    /**
     * Unsubscribes a user from event notifications.
     *
     * @param o the user to unsubscribe
     */
    public abstract void unsubscribe(User o);
}
