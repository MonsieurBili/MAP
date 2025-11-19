package Domain;

import Domain.Ducks.SwimmingDuck;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a race event for swimming ducks.
 * Extends Event with specific functionality for duck racing including lanes and race management.
 */
public class RaceEvent extends Event<SwimmingDuck> {

    private List<User> subscribers;
    private List<Double> culoare; // lanes/courses
    
    /**
     * Constructs a new RaceEvent with the specified name and location.
     *
     * @param name     the name of the race event
     * @param location the location where the race takes place
     */
    public RaceEvent(String name, String location) {
        super(name, location);
        this.subscribers = new ArrayList<>();
        this.culoare = new ArrayList<>();
    }

    /**
     * Gets the list of lane lengths for this race.
     *
     * @return the list of lane lengths
     */
    public List<Double> getCuloare() {
        return culoare;
    }
    
    /**
     * Adds a lane with the specified length to this race.
     *
     * @param cl the length of the lane to add
     */
    public void addculoar(double cl) {
        culoare.add(cl);
    }
    
    /**
     * Adds a duck as a participant in this race.
     *
     * @param ducky the swimming duck to add as a participant
     */
    public void addParticipant(SwimmingDuck ducky) {
        participants.add(ducky);
    }

    /**
     * Removes a duck from the race participants.
     *
     * @param ducky the swimming duck to remove
     */
    public void removeParticipant(SwimmingDuck ducky) {
        participants.remove(ducky);
    }

    /**
     * Starts the race event by notifying all observers.
     */
    public void startEvent() {
        notifyObservers();
    }

    @Override
    public void subscribe(User o) {
        subscribers.add(o);
    }

    @Override
    public void unsubscribe(User o) {
        subscribers.remove(o);
    }

    @Override
    public String toString() {
        return this.getId() + " The event " + this.getName() + 
               " is taking place in " + this.getLocation() + 
               " and has " + this.getCuloare().size() + " lanes";
    }
}
