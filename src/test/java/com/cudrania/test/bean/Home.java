package com.cudrania.test.bean;

import java.util.List;

public class Home {

	private List<User> users;
	
	private String address;


	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}

    @Override
    public String toString() {
        return "Home{" +
                "users=" + users +
                ", address='" + address + '\'' +
                '}';
    }
}
