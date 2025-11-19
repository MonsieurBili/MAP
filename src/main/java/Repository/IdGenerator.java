package Repository;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final IdGenerator INSTANCE = new IdGenerator();
    private final AtomicLong counter = new AtomicLong(1);

    private IdGenerator() {}

    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    public long nextId() {
        return counter.getAndIncrement();
    }
}
