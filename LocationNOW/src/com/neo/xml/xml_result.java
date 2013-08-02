package com.neo.xml;

public class xml_result {
	private String name;
	private String address;

	public xml_result() {
	}  

	public xml_result(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "name=" + name + ",address=" + address;
	}

}
