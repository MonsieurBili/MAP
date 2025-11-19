package Validators;

import Domain.Ducks.Duck;
import Exception.ValidationException;
import org.example.Constants;

/**
 * Validator for Duck entities.
 * Validates username, password, email, speed, and resistance.
 */
public class DuckValidator implements Validator<Duck> {
    
    /**
     * Validates a Duck entity according to business rules.
     *
     * @param duck the duck entity to validate
     * @throws ValidationException if validation fails with detailed error messages
     */
    @Override
    public void validate(Duck duck) throws ValidationException {
        StringBuilder errors = new StringBuilder();
        
        if (duck == null) {
            throw new ValidationException("Duck cannot be null.");
        }
        
        if (duck.getUsername().length() < Constants.MIN_USERNAME_LENGTH) {
            errors.append("Duck's username is too short (minimum ")
                  .append(Constants.MIN_USERNAME_LENGTH)
                  .append(" characters)\n");
        }
        
        if (duck.getPassword().length() < Constants.MIN_PASSWORD_LENGTH) {
            errors.append("Duck's password must have at least ")
                  .append(Constants.MIN_PASSWORD_LENGTH)
                  .append(" characters\n");
        }
        
        if (!duck.getEmail().matches(Constants.EMAIL_REGEX)) {
            errors.append("Duck's email is invalid\n");
        }
        
        if (duck.getViteza() < 0) {
            errors.append("Duck's speed cannot be negative\n");
        }
        
        if (duck.getRezistenta() < 0) {
            errors.append("Duck's resistance cannot be negative\n");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
