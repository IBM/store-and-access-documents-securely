package com.example.savings.rest;

import jakarta.servlet.http.Part;

public class SavingsAccount {
	
	private String username;
	private String firstname;
	private String lastname;
	private String mobilenumber;
	private String address;
	private String nationalid;
	private String emailid;
	private Part idprooffile;
	private Part addressprooffile;
	
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getMobilenumber() {
		return mobilenumber;
	}
	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getNationalid() {
		return nationalid;
	}
	public void setNationalid(String nationalid) {
		this.nationalid = nationalid;
	}
	public String getEmailid() {
		return emailid;
	}
	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}
	public Part getIdprooffile() {
		return idprooffile;
	}
	public void setIdprooffile(Part idprooffile) {
		this.idprooffile = idprooffile;
	}
	public Part getAddressprooffile() {
		return addressprooffile;
	}
	public void setAddressprooffile(Part addressprooffile) {
		this.addressprooffile = addressprooffile;
	}
	

}
