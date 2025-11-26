package Service;

import Domain.Ducks.Card;
import Domain.Ducks.Duck;
import Repository.IdGenerator;
import Repository.Repository;
import Repository.RepositoryCard;

public class ServiceCard extends ServiceEntity<Long, Card>{
    IdGenerator idGenerator;
    Repository<Long,Card> repositoryCard;
    public ServiceCard(IdGenerator idGenerator, Repository<Long,Card> repositoryCard) {
        super(repositoryCard);
        this.idGenerator = idGenerator;
        this.repositoryCard = repositoryCard;
    }

    @Override
    public Card save(Card card)
    {
        repositoryCard.save(card);
        return card;
    }
}
