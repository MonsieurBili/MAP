package Service;

import Domain.Friendship;
import Repository.FriendshipRepository;
import Repository.IdGenerator;

public class ServiceFriendship extends ServiceEntity<Long,Friendship> {
    IdGenerator idGenerator;
    FriendshipRepository friendshipRepository;
    public ServiceFriendship(IdGenerator idGenerator,FriendshipRepository friendshipRepository) {
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
