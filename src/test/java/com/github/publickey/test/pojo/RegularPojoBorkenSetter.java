package com.github.publickey.test.pojo;

public class RegularPojoBorkenSetter {
    private String stringValue;
    private double doubleValue;
    private int intValue;
    
    public String getStringValue() {
        return stringValue;
    }
    public void setStringValue(String stringValue) {
        // this is a bug
        stringValue = stringValue;
    }
    public double getDoubleValue() {
        return doubleValue;
    }
    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }
    public int getIntValue() {
        return intValue;
    }
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}
