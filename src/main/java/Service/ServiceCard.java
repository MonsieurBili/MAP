package Service;

import Domain.Ducks.Card;
import Domain.Ducks.Duck;
import Repository.IdGenerator;
import Repository.RepositoryCard;

public class ServiceCard extends ServiceEntity<Long, Card>{
    IdGenerator idGenerator;
    RepositoryCard repositoryCard;
    public ServiceCard(IdGenerator idGenerator, RepositoryCard repositoryCard) {
        super(repositoryCard);
        this.idGenerator = idGenerator;
        this.repositoryCard = repositoryCard;
    }

    @Override
    public Card save(Card card)
    {
        card.setId(idGenerator.nextId());
        repositoryCard.save(card);
        return card;
    }
}
