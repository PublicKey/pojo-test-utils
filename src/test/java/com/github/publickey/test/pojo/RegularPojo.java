package com.github.publickey.test.pojo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegularPojo {
	private String stringValue;
	private double doubleValue;
	private int intValue;
	private List<String> list;
	private Map<String, String> map;
	private Set<String> set;
	private int setOnlyIntValue;

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

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public Set<String> getSet() {
		return set;
	}

	public void setSet(Set<String> set) {
		this.set = set;
	}

	public void setSetOnlyIntValue(int setOnlyIntValue) {
		this.setOnlyIntValue = setOnlyIntValue;
	}

	/**
	 * non-pojo method
	 * 
	 * @return
	 */
	public int compute() {
		return setOnlyIntValue + intValue;
	}

	/**
	 * non-pojo method
	 * 
	 * @return
	 */
	public boolean isValid() {
		return intValue > 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(doubleValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + intValue;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((set == null) ? 0 : set.hashCode());
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
		RegularPojo other = (RegularPojo) obj;
		if (Double.doubleToLongBits(doubleValue) != Double.doubleToLongBits(other.doubleValue))
			return false;
		if (intValue != other.intValue)
			return false;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		if (set == null) {
			if (other.set != null)
				return false;
		} else if (!set.equals(other.set))
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
				+ ", list=" + list + ", map=" + map + ", set=" + set + "]";
	}

}
