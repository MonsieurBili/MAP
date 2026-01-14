package Service;

import Domain.User;
import Repository.Database.RepositoryDuckDB;
import Repository.Database.RepositoryPersonDB;
import util.PasswordEncoder;

import java.util.Optional;


public class ServiceAuth {
    private final RepositoryPersonDB personRepository;
    private final RepositoryDuckDB duckRepository;
    private User currentUser = null;

    public ServiceAuth(RepositoryPersonDB personRepository, RepositoryDuckDB duckRepository) {
        this.personRepository = personRepository;
        this.duckRepository = duckRepository;
    }

    public Optional<User> login(String username, String plainPassword) {
        if (username == null || username.isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            return Optional.empty();
        }

        User user = findByUsername(username);

        if (user != null && PasswordEncoder.matches(plainPassword, user.getPassword())) {
            currentUser = user;
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public User findByUsername(String username) {
        for (var person : personRepository.findAll()) {
            if (person.getUsername().equals(username)) {
                return person;
            }
        }
        for (var duck : duckRepository.findAll()) {
            if (duck.getUsername().equals(username)) {
                return duck;
            }
        }

        return null;
    }

    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }
}
