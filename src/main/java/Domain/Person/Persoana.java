package Domain.Person;

import Domain.User;

import java.time.LocalDate;

/**
 * Represents a person user in the system.
 * Extends User with person-specific attributes like name, date of birth, and occupation.
 */
public class Persoana extends User {
    
    private String nume;
    private String prenume;
    private LocalDate dataNasterii;
    private String ocupatie;
    private int nivelEmpatie;

    /**
     * Constructs a new Persoana (Person) with the specified attributes.
     *
     * @param username     the username for this person
     * @param email        the email address for this person
     * @param password     the password for this person
     * @param nume         the last name (surname)
     * @param prenume      the first name (given name)
     * @param dataNasterii the date of birth
     * @param ocupatie     the occupation/job
     */
    public Persoana(String username, String email, String password, String nume, 
                    String prenume, LocalDate dataNasterii, String ocupatie) {
        super(username, email, password);
        this.nume = nume;
        this.prenume = prenume;
        this.dataNasterii = dataNasterii;
        this.ocupatie = ocupatie;
        this.nivelEmpatie = 0;
    }
    
    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getNume() {
        return nume;
    }
    
    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getPrenume() {
        return prenume;
    }
    
    /**
     * Gets the date of birth.
     *
     * @return the date of birth
     */
    public LocalDate getDataNasterii() {
        return dataNasterii;
    }
    
    /**
     * Gets the occupation.
     *
     * @return the occupation
     */
    public String getOcupatie() {
        return ocupatie;
    }
    
    /**
     * Gets the empathy level.
     *
     * @return the empathy level
     */
    public int getNivelEmpatie() {
        return nivelEmpatie;
    }

    @Override
    public String toString() {
        return "Persoana{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", dataNasterii=" + dataNasterii +
                ", ocupatie='" + ocupatie + '\'' +
                ", nivelEmpatie=" + nivelEmpatie +
                '}';
    }
}
