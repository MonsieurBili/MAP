package Service;

import Domain.Friendship;
import Domain.Person.Persoana;
import Repository.FriendshipRepository;
import Repository.IdGenerator;
import Repository.Repository;

public class ServiceFriendship extends ServiceEntity<Long,Friendship> {
    IdGenerator idGenerator;
    private Repository<Long, Friendship> repository;
    public ServiceFriendship(IdGenerator idGenerator,Repository<Long,Friendship> friendshipRepository) {
        super(friendshipRepository);
        this.idGenerator = idGenerator;
        this.repository = friendshipRepository;
    }

    @Override
    public Friendship save(Friendship friendship) {
        friendship.setId(idGenerator.nextId());
        super.save(friendship);
        return friendship;
    }



}
