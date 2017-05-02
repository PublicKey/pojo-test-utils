package com.github.publickey.test.pojo;

public class ImmutablePojo {
    private final String stringValue;
    private final double doubleValue;
    private final int intValue;

    public ImmutablePojo(String stringValue, double doubleValue) {
        super();
        this.stringValue = stringValue;
        this.doubleValue = doubleValue;
        this.intValue = -1;
    }

    public ImmutablePojo(String stringValue, double doubleValue, int intValue) {
        super();
        this.stringValue = stringValue;
        this.doubleValue = doubleValue;
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public int getIntValue() {
        return intValue;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(doubleValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + intValue;
        result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImmutablePojo other = (ImmutablePojo) obj;
        if (Double.doubleToLongBits(doubleValue) != Double.doubleToLongBits(other.doubleValue))
            return false;
        if (intValue != other.intValue)
            return false;
        if (stringValue == null) {
            if (other.stringValue != null)
                return false;
        } else if (!stringValue.equals(other.stringValue))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RegularPojo [stringValue=" + stringValue + ", doubleValue=" + doubleValue + ", intValue=" + intValue
                + "]";
    }
}
