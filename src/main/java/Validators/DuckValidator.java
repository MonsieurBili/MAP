package Validators;

import Domain.Ducks.Duck;
import Exception.ValidationException;

public class DuckValidator implements Validator<Duck> {
    @Override
    public void validate(Duck duck) throws ValidationException
    {
        StringBuilder errors = new StringBuilder();
        if (duck == null)
            throw new ValidationException("Duck cannot be null.");
        if (duck.getUsername().length() < 2)
            errors.append("Duck's username is too short\n");
        if(duck.getPassword().length() < 6)
            errors.append("Duck's password must have atleast 6 characters\n");
        if (!duck.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            errors.append("Duck's email is invalid\n");
        if (duck.getViteza() < 0)
            errors.append("Duck's speed is invalid can't be lower than 0\n");
        if (duck.getRezistenta() < 0)
            errors.append("Duck's resistance is invalid can't be lower than 0\n");
        if (duck.getTipRata() == null)
            errors.append("Duck's type is invalid\n");
        if (!errors.isEmpty())
            throw new ValidationException(errors.toString());
    }

}
