package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import Observer.Observer;

/**
 * Abstract base class for all user types in the system.
 * Represents a user with basic authentication and social networking capabilities.
 */
public abstract class User extends Entity<Long> implements Observer {
    
    private String username;
    private String email;
    private String password;
    private List<User> friends;

    /**
     * Constructs a new User with the specified credentials.
     *
     * @param username the username for this user
     * @param email    the email address for this user
     * @param password the password for this user
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.friends = new ArrayList<>();
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the list of friends for this user.
     *
     * @return an unmodifiable view of the friends list
     */
    public List<User> getFriends() {
        return friends;
    }

    /**
     * Adds a friend to this user's friend list.
     * The friendship is bidirectional.
     *
     * @param user the user to add as a friend
     * @throws NullPointerException     if user is null
     * @throws IllegalArgumentException if user is already a friend
     */
    public void addFriend(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }
        if (friends.contains(user)) {
            throw new IllegalArgumentException("User is already a friend");
        }
        
        friends.add(user);
        
        // Ensure bidirectional relationship
        if (!user.getFriends().contains(this)) {
            user.getFriends().add(this);
        }
    }

    /**
     * Removes a friend from this user's friend list.
     * The removal is bidirectional.
     *
     * @param user the user to remove from friends
     * @throws NullPointerException if user is null
     */
    public void removeFriend(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }
        
        friends.remove(user);
        
        // Ensure bidirectional removal
        user.getFriends().remove(this);
    }

    @Override
    public void update() {
        System.out.println("Update User");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && 
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, email);
    }
}
