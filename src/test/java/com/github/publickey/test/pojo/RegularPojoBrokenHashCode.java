package com.github.publickey.test.pojo;

public class RegularPojoBrokenHashCode {
	private String stringValue;
	private double doubleValue;
	private int intValue;

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
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

	@Override
	public int hashCode() {
		// broken hash code, it should not change
		intValue++;
		return intValue;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegularPojoBrokenHashCode other = (RegularPojoBrokenHashCode) obj;
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
