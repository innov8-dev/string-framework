package dev.innov8.exceptions;

public class NoSuchPropertyException extends RuntimeException {

    public NoSuchPropertyException(String propertyName) {
        super("No such property named, " + propertyName + ", found on the provided bean!");
    }
}
