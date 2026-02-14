package org.example.command.fields;

import org.example.command.exceptions.ValidationException;

public abstract class Field<T> {
    private final String name;
    private final String description;
    private final boolean required;
    private final T defaultValue;
    private final Class<T> type;
    private T value = null;

    protected Field(String name, String description, boolean required, T defaultValue, Class<T> type) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public void setRawValue(String rawString) throws ValidationException {
        this.value = validate(rawString);
    }

    public void setValue(T value) {
        this.value = value;
    }
    public T getValue() {
        return value;
    }

    protected abstract T validate(String rawString) throws ValidationException;

    public boolean isValid(String rawString) {
        try {
            var _ = validate(rawString);
        } catch (ValidationException e) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Class<T> getType() {
        return type;
    }
}
