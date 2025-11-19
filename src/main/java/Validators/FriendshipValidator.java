package Validators;

import Domain.Friendship;
import Exception.ValidationException;

public class FriendshipValidator implements Validator<Friendship> {
    StringBuilder errors = new StringBuilder();
    @Override
    public void validate(Friendship friendship) {
        if (friendship.getUser1().getId().equals(friendship.getUser2().getId())) {
            errors.append("An user can't befriend himself");
        }
        if (!errors.isEmpty())
            throw new ValidationException(errors.toString());
    }
}
