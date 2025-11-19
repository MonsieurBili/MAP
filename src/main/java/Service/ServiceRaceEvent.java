package Service;

import Domain.Ducks.SwimmingDuck;
import Domain.RaceEvent;
import Domain.User;
import Repository.IdGenerator;
import Repository.RepositoryRaceEvent;

import java.util.ArrayList;
import java.util.List;

public class ServiceRaceEvent extends ServiceEntity<Long, RaceEvent> {
    IdGenerator idGenerator;
    RepositoryRaceEvent repository;

    public ServiceRaceEvent(RepositoryRaceEvent repository) {
        super(repository);
        this.idGenerator = IdGenerator.getInstance();
        this.repository = repository;
    }
    @Override
    public RaceEvent save(RaceEvent event)
    {
        event.setId(idGenerator.nextId());
        repository.save(event);
        return event;
    }

    public List<SwimmingDuck> solve(RaceEvent event) {
        int lungr = event.getParticipants().size();
        int lungc = event.getCuloare().size();
        double minspeed = Integer.MAX_VALUE;

        List<SwimmingDuck> Ducks = event.getParticipants();
        for (SwimmingDuck duck : Ducks) {
            if (duck.getViteza() < minspeed) {
                minspeed = duck.getViteza();
            }
        }
        int n = Ducks.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                boolean swap = false;
                if (Ducks.get(j).getRezistenta() > Ducks.get(j + 1).getRezistenta()) {
                    swap = true;
                }
                // If rezistenta equal → compare viteza
                else if (Ducks.get(j).getRezistenta() == Ducks.get(j + 1).getRezistenta() &&
                        Ducks.get(j).getViteza() > Ducks.get(j + 1).getViteza()) {
                    swap = true;
                }
                if (swap) {
                    SwimmingDuck temp = Ducks.get(j);
                    Ducks.set(j, Ducks.get(j + 1));
                    Ducks.set(j + 1, temp);
                }
            }
        }
        List<Double> culoare = event.getCuloare();
        double maxtime = culoare.get(culoare.size() - 1) * 2 / minspeed;
        double mintime = 0;
        List<SwimmingDuck> winners = new ArrayList<>();
        List<SwimmingDuck> result = new ArrayList<>();
        while (maxtime - mintime > 0.001) {
            double mij = (maxtime + mintime) / 2.0;
            result = canFinish(mij, Ducks, culoare);

            if (result != null) {
                maxtime = mij;
                winners = result;
            } else
                mintime = mij;
        }
        event.notifyObservers();
        return winners;
    }

private List<SwimmingDuck> canFinish(double currentTime, List<SwimmingDuck> ducks, List<Double> lanes)
{
    int nDucks = ducks.size();
    int nLanes = lanes.size();

    List<SwimmingDuck> winners = new ArrayList<>();
    boolean[] used = new boolean[nDucks];

    double tempTime = 0;
    int current = 0;

    for (int i = 0; i < nLanes; i++) {
        boolean found = false;

        for (int j = current; j < nDucks; j++) {
            if (!used[j]) {
                double timeNeeded = lanes.get(i) * 2.0 / ducks.get(j).getViteza();
                if (timeNeeded <= currentTime + 0.001) {
                    if (tempTime < timeNeeded)
                        tempTime = timeNeeded;
                    winners.add(ducks.get(j));
                    used[j] = true;
                    current = j + 1;
                    found = true;
                    break;
                }
            }
        }
        if (!found)
            return null;
    }

    return winners;
}
}
