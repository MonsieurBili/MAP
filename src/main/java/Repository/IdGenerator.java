package Repository;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe singleton ID generator for entities.
 * Uses an atomic counter to ensure unique, sequential IDs across the application.
 */
public class IdGenerator {
    
    private static final IdGenerator INSTANCE = new IdGenerator();
    private final AtomicLong counter = new AtomicLong(1);

    /**
     * Private constructor to prevent external instantiation.
     */
    private IdGenerator() {}

    /**
     * Gets the singleton instance of the ID generator.
     *
     * @return the singleton IdGenerator instance
     */
    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * Generates and returns the next unique ID.
     * This method is thread-safe.
     *
     * @return the next unique ID as a long value
     */
    public long nextId() {
        return counter.getAndIncrement();
    }
}
