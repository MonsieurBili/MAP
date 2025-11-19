package Repository;

import Domain.Ducks.Card;
import Domain.Ducks.Duck;
import Validators.CardValidator;

public class RepositoryCard extends  RepositoryEntity<Long, Card>{
    public RepositoryCard(CardValidator validator)
    {
        super(validator);
    }
}
