package Domain.Person;

import Domain.UserFactory;

import java.time.LocalDate;

public class PersonFactory implements UserFactory<Persoana> {
    private String username;
    private String email;
    private String password;
    private String nume;
    private String prenume;
    private LocalDate dataNasterii;
    private String ocupatie;
    private int nivelEmpatie;

    public PersonFactory(){};


    public void setData(String username,String email,String password,String nume,String prenume,LocalDate dataNasterii,String ocupatie)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nume = nume;
        this.prenume = prenume;
        this.dataNasterii = dataNasterii;
        this.ocupatie = ocupatie;
    }
    @Override
    public Persoana createUser() {
        return new Persoana(username,email,password,nume,prenume,dataNasterii,ocupatie);
    }

}
