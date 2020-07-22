package dev.innov8.services;

public class PrototypeService {

    private String someValue;
    private int intValue;

    public String getSomeValue() {
        System.out.println("PrototypeService.getSomeValue() invoked!");
        return someValue;
    }

    public void setSomeValue(String someValue) {
        System.out.println("PrototypeService.setSomeValue(String) invoked!");
        this.someValue = someValue;
    }

    public int getIntValue() {
        System.out.println("PrototypeService.getIntValue() invoked!");
        return intValue;
    }

    public void setIntValue(int intValue) {
        System.out.println("PrototypeService.setIntValue(int) invoked!");
        this.intValue = intValue;
    }

}
