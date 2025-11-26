package Repository;

import Domain.Ducks.Duck;
import Domain.Person.Persoana;
import Domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite repository that combines Person and Duck repositories for user lookups.
 * This is useful when you need to find any user by ID regardless of their type.
 */
public class CompositeUserRepository implements Repository<Long, User> {
    private final Repository<Long, Persoana> personRepository;
    private final Repository<Long, Duck> duckRepository;

    public CompositeUserRepository(Repository<Long, Persoana> personRepository, Repository<Long, Duck> duckRepository) {
        this.personRepository = personRepository;
        this.duckRepository = duckRepository;
    }

    @Override
    public User findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");

        // Try to find in person repository first
        User user = personRepository.findOne(id);
        if (user != null) return user;

        // Then try duck repository
        return duckRepository.findOne(id);
    }

    @Override
    public Iterable<User> findAll() {
        List<User> allUsers = new ArrayList<>();

        for (Persoana p : personRepository.findAll()) {
            allUsers.add(p);
        }

        for (Duck d : duckRepository.findAll()) {
            allUsers.add(d);
        }

        return allUsers;
    }

    @Override
    public User save(User entity) {
        throw new UnsupportedOperationException("CompositeUserRepository does not support save. Use specific repository instead.");
    }

    @Override
    public User delete(Long id) {
        throw new UnsupportedOperationException("CompositeUserRepository does not support delete. Use specific repository instead.");
    }

    @Override
    public User update(User entity) {
        throw new UnsupportedOperationException("CompositeUserRepository does not support update. Use specific repository instead.");
    }
}
