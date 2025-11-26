package Repository.Database;

import Domain.Friendship;
import Repository.Repository;
import Validators.FriendshipValidator;
import Validators.Validator;

public class RepositoryFriendshipDb implements Repository<Long, Friendship> {
    private final FriendshipValidator validator;

    public  RepositoryFriendshipDb(FriendshipValidator validator) {
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        return null;
    }

    @Override
    public Friendship save(Friendship entity) {
        return null;
    }

    @Override
    public Friendship delete(Long aLong) {
        return null;
    }

    @Override
    public Friendship update(Friendship entity) {
        return null;
    }

}
