package dev.innov8.exceptions;

public class UnresolvableDependencyException extends RuntimeException {

    public UnresolvableDependencyException(String beanName, Throwable cause) {
        super("The dependency with the name, " + beanName + ", could not be resolved. The nested exception is " + cause.getClass() + ".");
    }

}
