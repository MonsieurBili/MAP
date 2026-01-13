package Repository;

import Domain.Entity;
import util.paging.Page;
import util.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID,E> {
    Page<E> findAllOnPage(Pageable pageable);
}
