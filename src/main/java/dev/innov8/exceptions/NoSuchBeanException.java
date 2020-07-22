package dev.innov8.exceptions;

public class NoSuchBeanException extends RuntimeException {

    public NoSuchBeanException(String beanName) {
        super("No bean with name, " + beanName + ", found within the component registry!");
    }

}
