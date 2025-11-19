package Repository;

import Domain.RaceEvent;
import Validators.Validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class RepositoryRaceEvent extends RepositoryEntity<Long, RaceEvent>{
    private String filename;
    public RepositoryRaceEvent(String filename ,Validator<RaceEvent> validator) {
        super(validator);
        this.filename = filename;
        loaddata();
    }
    private void loaddata() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");

                if (parts.length < 3) continue;
                IdGenerator idGenerator= IdGenerator.getInstance();
                String name = parts[0];
                String location = parts[1];

                RaceEvent event = new RaceEvent(name, location);
                event.setId(idGenerator.nextId());


                List<Double> lanes = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    event.addculoar(Double.parseDouble(parts[i]));
                }
                this.save(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
