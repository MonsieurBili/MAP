package Observer;

/**
 * Observable interface for the Observer design pattern.
 * Classes implementing this interface can be observed by Observer objects.
 *
 * @param <T> the type of observers that can observe this object
 */
public interface Observable<T> {
    
    /**
     * Adds an observer to be notified of changes.
     *
     * @param o the observer to add
     */
    void addObserver(T o);
    
    /**
     * Removes an observer from the notification list.
     *
     * @param o the observer to remove
     */
    void removeObserver(T o);
    
    /**
     * Notifies all registered observers of a change.
     */
    void notifyObservers();
}
