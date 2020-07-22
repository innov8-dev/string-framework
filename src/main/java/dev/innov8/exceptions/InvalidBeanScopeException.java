package dev.innov8.exceptions;

public class InvalidBeanScopeException extends RuntimeException {

    public InvalidBeanScopeException(String providedScope) {
        super("The value, " + providedScope + ", was not recognized as a valid scope.");
    }

}
