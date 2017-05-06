package com.github.publickey.test.pojo;

public class RegularPojoBrokenEquals {
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
		// very poor equals implementation
		return true;
	}

	@Override
	public String toString() {
		return "RegularPojo [stringValue=" + stringValue + ", doubleValue=" + doubleValue + ", intValue=" + intValue
				+ "]";
	}
}
