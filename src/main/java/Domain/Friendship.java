package Domain;

/**
 * Represents a friendship relationship between two users.
 * A friendship is a bidirectional connection between user1 and user2.
 */
public class Friendship extends Entity<Long> {
    
    private User user1;
    private User user2;

    /**
     * Constructs a new Friendship between two users.
     *
     * @param user1 the first user in the friendship
     * @param user2 the second user in the friendship
     */
    public Friendship(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }
    
    /**
     * Gets the first user in this friendship.
     *
     * @return the first user
     */
    public User getUser1() {
        return user1;
    }
    
    /**
     * Gets the second user in this friendship.
     *
     * @return the second user
     */
    public User getUser2() {
        return user2;
    }
    
    @Override
    public String toString() {
        return this.getId() + " " + user1.getUsername() + " " + user2.getUsername();
    }
}
