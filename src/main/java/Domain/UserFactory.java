package Domain;

public interface UserFactory<E extends User> {
    E createUser();
}
