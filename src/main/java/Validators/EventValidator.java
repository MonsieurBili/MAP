package Validators;

import Domain.RaceEvent;
import Exception.ValidationException;

/**
 * Validator for RaceEvent entities.
 * Validates that race event properties meet business requirements.
 */
public class EventValidator implements Validator<RaceEvent> {
    
    /**
     * Validates a RaceEvent entity according to business rules.
     *
     * @param event the race event entity to validate
     * @throws ValidationException if validation fails with detailed error messages
     */
    @Override
    public void validate(RaceEvent event) throws ValidationException {
        StringBuilder errors = new StringBuilder();
        
        if (event == null) {
            throw new ValidationException("RaceEvent cannot be null");
        }
        
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            errors.append("Event name cannot be empty\n");
        }
        
        if (event.getLocation() == null || event.getLocation().trim().isEmpty()) {
            errors.append("Event location cannot be empty\n");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
