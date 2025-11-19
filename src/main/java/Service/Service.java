package Service;

import Domain.Entity;

public interface Service <ID,E extends Entity<ID>> {


    E findOne(ID id);

    Iterable<E> findAll();

    E save(E entity);

    E update(E entity);

    E delete(ID id);


}
