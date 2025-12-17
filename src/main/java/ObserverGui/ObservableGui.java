package ObserverGui;

import util.ChangeEvent;

public interface ObservableGui<E extends ChangeEvent> {
    void addObserver(ObserverGui<E> e);
    void removeObserver(ObserverGui<E> e);
    void notifyObservers(E e);
}
