package dev.innov8.exceptions;

public class NonUniqueBeanNameException extends RuntimeException {

    public NonUniqueBeanNameException(String duplicatedBeanName) {
        super("There is already a bean within the registry with the name, " + duplicatedBeanName + "!");
    }
}
