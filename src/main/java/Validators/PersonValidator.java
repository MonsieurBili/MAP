package Validators;

import Domain.Person.Persoana;
import Exception.ValidationException;
import org.example.Constants;

import java.time.LocalDate;

/**
 * Validator for Person entities.
 * Validates username, password, email, name fields, and date of birth.
 */
public class PersonValidator implements Validator<Persoana> {
    
    /**
     * Validates a Person entity according to business rules.
     * 
     * @param person the person entity to validate
     * @throws ValidationException if validation fails with detailed error messages
     */
    @Override
    public void validate(Persoana person) throws ValidationException {
        StringBuilder errors = new StringBuilder();
        
        if (person == null) {
            throw new ValidationException("Person can't be null");
        }
        
        if (person.getUsername().length() < Constants.MIN_USERNAME_LENGTH) {
            errors.append("Person's username is too short (minimum ")
                  .append(Constants.MIN_USERNAME_LENGTH)
                  .append(" characters)\n");
        }
        
        if (person.getPassword().length() < Constants.MIN_PASSWORD_LENGTH) {
            errors.append("Person's password must have at least ")
                  .append(Constants.MIN_PASSWORD_LENGTH)
                  .append(" characters\n");
        }
        
        if (!person.getEmail().matches(Constants.EMAIL_REGEX)) {
            errors.append("Person's email is invalid\n");
        }
        
        if (person.getNume().length() < Constants.MIN_NAME_LENGTH) {
            errors.append("Person's last name is too short (minimum ")
                  .append(Constants.MIN_NAME_LENGTH)
                  .append(" characters)\n");
        }
        
        if (person.getPrenume().length() < Constants.MIN_NAME_LENGTH) {
            errors.append("Person's first name is too short (minimum ")
                  .append(Constants.MIN_NAME_LENGTH)
                  .append(" characters)\n");
        }
        
        LocalDate dob = person.getDataNasterii();
        if (dob == null) {
            errors.append("Date of birth cannot be null.\n");
        } else if (dob.isAfter(LocalDate.now())) {
            errors.append("Date of birth cannot be in the future.\n");
        }
        
        if (person.getNivelEmpatie() < 0) {
            errors.append("Person's empathy level cannot be negative.\n");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
