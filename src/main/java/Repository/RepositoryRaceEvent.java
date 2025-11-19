package Repository;

import Domain.RaceEvent;
import Validators.Validator;

import java.io.BufferedReader;
import java.io.FileReader;

public class RepositoryRaceEvent extends RepositoryEntity<Long, RaceEvent>{
    private String filename;
    public RepositoryRaceEvent(String filename ,Validator<RaceEvent> validator) {
        super(validator);
        this.filename = filename;
    }
    /*public void loaddata(String filename)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    for (String part : parts) {
                        float cl =  Float.parseFloat(part);

                    }
            }
    }*/


}
