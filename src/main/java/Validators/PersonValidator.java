package Validators;

import Domain.Person.Persoana;
import Exception.ValidationException;

import java.time.LocalDate;

public class PersonValidator implements Validator<Persoana>{
    @Override
    public void validate(Persoana person)throws ValidationException
    {
        StringBuilder errors = new StringBuilder();
        if (person == null)
            throw new ValidationException("Person can't be null");
        if (person.getUsername().length() < 2)
            errors.append("Person's username is too short\n");
        if(person.getPassword().length() < 6)
            errors.append("Person's password must have atleast 6 characters\n");
        if (!person.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            errors.append("Person's email is invalid\n");
        if(person.getNume().length() < 4)
            errors.append("Person's last name is too short\n");
        if(person.getPrenume().length()<4)
            errors.append("Person's first name is too short\n");
        LocalDate dob = person.getDataNasterii();
        if (dob == null) {
            errors.append("Date of birth cannot be null.\n");
        } else if (dob.isAfter(LocalDate.now())) {
            errors.append("Date of birth cannot be in the future.\n");
        }
        if (person.getNivelEmpatie() < 0)
            errors.append("Person's nivel empatie cannot be negative.\n");
        if (!errors.isEmpty())
            throw new ValidationException(errors.toString());

    }
}
