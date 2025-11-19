package Validators;

import Exception.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
