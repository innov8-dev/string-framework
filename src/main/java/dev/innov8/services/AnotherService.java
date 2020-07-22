package dev.innov8.services;

public class AnotherService {

    private long longValue;

    public long getLongValue() {
        System.out.println("AnotherService.getLongValue() invoked!");
        return longValue;
    }

    public void setLongValue(long longValue) {
        System.out.println("AnotherService.setLongValue(long) invoked!");
        this.longValue = longValue;
    }

}
