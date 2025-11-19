package Domain;

import Domain.Ducks.SwimmingDuck;

import java.util.ArrayList;
import java.util.List;

public class RaceEvent extends Event<SwimmingDuck>{

    private List<User> subscribers;
    private List<Double> culoare;
    public RaceEvent(String name,String location)
    {
        super(name,location);
        subscribers = new ArrayList<>();
        culoare = new ArrayList<>();
    }

    public List<Double>  getCuloare() {
        return culoare;
    }
    public void addculoar(double cl)
    {
        culoare.add(cl);
    }
    public void addParticipant(SwimmingDuck ducky)
    {
        participants.add(ducky);
    }

    public void removeParticipant(SwimmingDuck ducky)
    {
        participants.remove(ducky);
    }

    @Override
    public void subscribe(User o)
    {
        subscribers.add(o);
    }

    @Override
    public void unsubscribe(User o)
    {
        subscribers.remove(o);
    }
    @Override
    public void notifyObservers() {
       subscribers.forEach(o -> o.update());
    }
    @Override
    public String toString()
    {
        return this.getId()+ " The event" + this.getName() + " is taking place in " + this.getLocation() +" and has " + this.getCuloare().size() + " lanes";
    }
}
