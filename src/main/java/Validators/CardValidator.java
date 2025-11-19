package Validators;

import Domain.Ducks.Card;
import Exception.ValidationException;

/**
 * Validator for Card (flock) entities.
 * Validates that card/flock properties meet business requirements.
 */
public class CardValidator implements Validator<Card> {
    
    /**
     * Validates a Card entity according to business rules.
     *
     * @param card the card entity to validate
     * @throws ValidationException if validation fails with detailed error messages
     */
    @Override
    public void validate(Card card) throws ValidationException {
        StringBuilder errors = new StringBuilder();
        
        if (card == null) {
            throw new ValidationException("Card cannot be null");
        }
        
        if (card.getNumeCard() == null || card.getNumeCard().length() < 2) {
            errors.append("Flock name is too short (minimum 2 characters)\n");
        }
        
        if (card.getTip() == null) {
            errors.append("Flock type cannot be null\n");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
