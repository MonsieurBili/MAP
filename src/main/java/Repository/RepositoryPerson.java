package Repository;

import Domain.Person.Persoana;
import Validators.PersonValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

public class RepositoryPerson extends  RepositoryEntity<Long,Persoana>{
    private String filename;
    public RepositoryPerson(PersonValidator personValidator,String filename) {
        super(personValidator);
        this.filename = filename;
        loaddata(filename);
    }
    public void loaddata(String filename)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            IdGenerator idGenerator= IdGenerator.getInstance();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                String username = parts[0].trim();
                String password = parts[1].trim();
                String email = parts[2].trim();
                String lastName = parts[3].trim();
                String firstName = parts[4].trim();
                LocalDate dateOfBirth = LocalDate.parse(parts[5].trim());
                String occupation = parts[6].trim();
                Persoana p = new Persoana(username, email,password,
                        lastName, firstName, dateOfBirth, occupation);
                p.setId(idGenerator.nextId());
                super.save(p);
            }
        }
        catch(IOException e) {
            System.out.println("Error at reading");
        }
    }
}
