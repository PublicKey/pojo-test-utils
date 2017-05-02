package com.github.publickey.test.pojo;

public enum EnumPojo {
    ELEM1,
    ELEM2,
    ELEM3(88),
    ELEM4;
    
    private int value;
    
    private EnumPojo() {
        value = ordinal();
    }
    
    private EnumPojo(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
