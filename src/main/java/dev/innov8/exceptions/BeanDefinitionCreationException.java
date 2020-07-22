package dev.innov8.exceptions;

public class BeanDefinitionCreationException extends RuntimeException {

    public BeanDefinitionCreationException(String beanName, Throwable cause) {
        super("Bean definition for the bean with name, " + beanName + ", could not be created. The nested exception is " + cause.getClass() + ".", cause);
    }
}
