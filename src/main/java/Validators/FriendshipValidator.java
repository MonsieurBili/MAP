package Validators;

import Domain.Friendship;
import Exception.ValidationException;

/**
 * Validator for Friendship entities.
 * Ensures that friendship relationships are valid and logical.
 */
public class FriendshipValidator implements Validator<Friendship> {
    
    /**
     * Validates a Friendship entity according to business rules.
     * A user cannot be friends with themselves.
     *
     * @param friendship the friendship entity to validate
     * @throws ValidationException if validation fails with detailed error messages
     */
    @Override
    public void validate(Friendship friendship) {
        StringBuilder errors = new StringBuilder();
        
        if (friendship == null) {
            throw new ValidationException("Friendship cannot be null");
        }
        
        if (friendship.getUser1() == null || friendship.getUser2() == null) {
            errors.append("Friendship users cannot be null\n");
        } else if (friendship.getUser1().getId().equals(friendship.getUser2().getId())) {
            errors.append("A user cannot befriend themselves\n");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
