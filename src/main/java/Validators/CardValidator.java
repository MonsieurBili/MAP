package Validators;

import Domain.Ducks.Card;
import Domain.Ducks.Duck;
import Exception.ValidationException;

public class CardValidator implements  Validator<Card>{
    private StringBuilder errors = new StringBuilder();
    @Override
    public void validate(Card card) throws ValidationException
    {
        if (card.getNumeCard().length() < 2)
        {
            errors.append("Flock name is too short\n");
        }
        if (!errors.isEmpty())
            throw new ValidationException(errors.toString());
    }
}
