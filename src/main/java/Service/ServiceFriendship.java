package Service;

import Domain.Friendship;
import Repository.Repository;
import Repository.IdGenerator;

public class ServiceFriendship extends ServiceEntity<Long,Friendship> {
    IdGenerator idGenerator;
    Repository<Long, Friendship> friendshipRepository;

    public ServiceFriendship(IdGenerator idGenerator, Repository<Long, Friendship> friendshipRepository) {
        super(friendshipRepository);
        this.idGenerator = idGenerator;
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public Friendship save(Friendship friendship) {
        friendship.setId(idGenerator.nextId());
        super.save(friendship);
        return friendship;
    }
}
