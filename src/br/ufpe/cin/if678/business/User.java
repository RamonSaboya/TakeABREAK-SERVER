package br.ufpe.cin.if678.business;

import java.util.Date;

public class User { //Implementar imagens

	private int ID;
	private String username;
	private String password;
	private String email;
	private Date birthdate;
	private String fullName;
	private String cellphone;

	public User(int ID, String username, String password, String email, Date birthdate, String fullName, String cellphone) {
		this.ID = ID;
		this.username = username;
		this.password = password;
		this.email = email;
		this.birthdate = birthdate;
		this.fullName = fullName;
		this.cellphone = cellphone;
	}

	public int getID() {
		return this.ID;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getEmail() {
		return this.email;
	}

	public Date getBirthdate() {
		return this.birthdate;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getCellphone() {
		return this.cellphone;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

}
