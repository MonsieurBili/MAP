package Domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Base entity class for all domain objects with an identifier.
 * 
 * @param <ID> the type of the entity identifier
 */
public class Entity<ID> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7331115341259248461L;
    
    private ID id;

    /**
     * Default constructor.
     */
    public Entity() {}

    /**
     * Gets the entity identifier.
     * 
     * @return the entity ID
     */
    public ID getId() {
        return id;
    }

    /**
     * Sets the entity identifier.
     * 
     * @param id the entity ID to set
     */
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


