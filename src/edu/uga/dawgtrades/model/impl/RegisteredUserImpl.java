package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.model.RegisteredUser;

public class RegisteredUserImpl extends Persistent implements RegisteredUser {

	private String username;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private String phone;
	private boolean isAdmin;
	private boolean canText;
	
	public RegisteredUserImpl(
							String username,
							String firstName,
							String lastName, 
							String password, 
							boolean isAdmin, 
							String phone, 
							String email, 
							boolean canText)
	{
		super(-1);
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.phone = phone;
		this.isAdmin = isAdmin;
		this.canText = canText;
	}
	
	public RegisteredUserImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return this.username;
	}

	@Override
	public void setName(String name) {
	
		//TODO Implement set name
		//first set firstname to first half of string
		//then set lastname to second half of string
	}

	@Override
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
		
	}

	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean getIsAdmin() {
		return this.isAdmin;
	}

	@Override
	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getPhone() {
		return this.phone;
	}

	@Override
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public boolean getCanText() {
		return this.canText;
	}

	@Override
	public void setCanText(boolean canText) {
		this.canText = canText;
		
	}

	

}
