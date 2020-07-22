package dev.innov8.exceptions;

public class BeanCreationException extends RuntimeException {

    public BeanCreationException(String beanName, Throwable cause) {
        super("Could not create bean with name, " + beanName + ", nested exception is " + cause.getClass() + ".", cause);
    }

}
