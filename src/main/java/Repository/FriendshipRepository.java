package Repository;

import Domain.Friendship;
import Domain.User;
import Validators.FriendshipValidator;

import java.io.BufferedReader;
import java.io.FileReader;

public class FriendshipRepository extends RepositoryEntity<Long, Friendship>{
    private String filename;
    public FriendshipRepository(FriendshipValidator friendshipValidator) {
        super(friendshipValidator);
    }
}
