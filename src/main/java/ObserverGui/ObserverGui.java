package ObserverGui;

import javafx.scene.control.TextFormatter;
import util.ChangeEvent;

public interface ObserverGui<E extends ChangeEvent> {
    void update(E e);
}
