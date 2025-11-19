package Observer;

/**
 * Observer interface for the Observer design pattern.
 * Classes implementing this interface can be notified of changes in observable objects.
 */
public interface Observer {
    
    /**
     * Called when the observed object changes.
     * Implementations should define how to respond to updates.
     */
    void update();
}
