package Repository;

import Domain.Ducks.Duck;
import Domain.Ducks.DuckFactory;
import Domain.Ducks.TipRata;
import Validators.DuckValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RepositoryDuck extends  RepositoryEntity<Long, Duck>{
    private String filename;
    public RepositoryDuck(DuckValidator validator,String filename) {
        super(validator);
        this.filename = filename;
        loaddata(filename);
    };
    public void loaddata(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            IdGenerator idGenerator = IdGenerator.getInstance();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                String username = parts[0].trim();
                String password = parts[1].trim();
                String email = parts[2].trim();
                String tipRata = parts[3].trim();
                double viteza = Double.parseDouble(parts[4].trim());
                double rezistenta = Double.parseDouble(parts[5].trim());
                TipRata tipulRatei;
                if (tipRata.equals("FLYING"))
                    tipulRatei = TipRata.FLYING;
                else if (tipRata.equals("SWIMMING"))
                    tipulRatei = TipRata.SWIMMING;
                else
                    tipulRatei = TipRata.FLYING_AND_SWIMMING;
                DuckFactory duckFactory = DuckFactory.getInstance();
                duckFactory.setData(username, email, password, tipulRatei, viteza, rezistenta);
                Duck d = duckFactory.createUser();
                d.setId(idGenerator.nextId());
                super.save(d);
            }
        } catch (IOException e) {
            System.out.println("Error at reading");
        }
    }
}
