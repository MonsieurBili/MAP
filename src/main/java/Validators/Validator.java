package Validators;

import Exception.ValidationException;

/**
 * Generic validator interface for validating entities.
 * Implementations should define specific validation rules for entity types.
 *
 * @param <T> the type of entity to validate
 */
public interface Validator<T> {
    
    /**
     * Validates the given entity according to business rules.
     *
     * @param entity the entity to validate
     * @throws ValidationException if the entity fails validation with detailed error messages
     */
    void validate(T entity) throws ValidationException;
}
