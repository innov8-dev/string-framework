package dev.innov8.exceptions;

public class SelfReferencingBeanException extends RuntimeException {

    public SelfReferencingBeanException(String beanName) {
        super("The bean with the name, " + beanName + ", has a circular reference to itself.");
    }

}
