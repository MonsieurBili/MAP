package Domain.Person;

import Domain.User;

import java.time.LocalDate;

public class Persoana extends User {
    private String nume;
    private String prenume;
    private LocalDate dataNasterii;
    private String ocupatie;
    private int nivelEmpatie;

    public Persoana(String username,String email,String password,String nume,String prenume,LocalDate dataNasterii,String ocupatie){
        super(username,email,password);
        this.nume = nume;
        this.prenume = prenume;
        this.dataNasterii = dataNasterii;
        this.ocupatie = ocupatie;
        nivelEmpatie = 0;
    }
    public String getNume() {
        return nume;
    }
    public String getPrenume() {
        return prenume;
    }
    public LocalDate getDataNasterii() {
        return dataNasterii;
    }
    public String getOcupatie() {
        return ocupatie;
    }
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
