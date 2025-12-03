package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Observer.Observer;

public abstract class User extends Entity<Long> implements Observer {
    private String username;
    private String email;
    private String password;
    private List<User> friends;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        friends = new ArrayList<User>();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<User> getFriends() { return friends; }
    public void addFriend(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        } else if (friends.contains(user))
            throw new IllegalArgumentException("User is already friend");
        friends.add(user);
        if (!user.getFriends().contains(this)) {
            user.getFriends().add(this);
        }
    }

    public void removeFriend(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }
        friends.remove(user);
        if (!user.getFriends().remove(this)) {
            user.getFriends().remove(this);
        }
    }

    @Override
    public void update()
    {
        System.out.println(this.getUsername() + " this user was a subscriber and was notified");
    }

    ///public abstract void login();
    ///public abstract void logout();
    /// public abstract void sendMessage();
    /// public abstract void receiveMessage();
    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || !(o instanceof User)) return false;

        User otherUser = (User) o;

        return Objects.equals(this.getId(), otherUser.getId());
    }
}
