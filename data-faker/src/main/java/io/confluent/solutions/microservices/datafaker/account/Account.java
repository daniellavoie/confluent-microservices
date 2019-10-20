package io.confluent.solutions.microservices.datafaker.account;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Account {
	private int number;
	private String firstName;
	private String lastName;
	private String streetAddress;
	private String numberAddress;
	private String cityAddress;
	private String countryAddress;

	public Account() {

	}

	public Account(int number, String firstName, String lastName, String streetAddress, String numberAddress,
			String cityAddress, String countryAddress) {
		this.number = number;
		this.firstName = firstName;
		this.lastName = lastName;
		this.streetAddress = streetAddress;
		this.numberAddress = numberAddress;
		this.cityAddress = cityAddress;
		this.countryAddress = countryAddress;
	}

	@Id
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getNumberAddress() {
		return numberAddress;
	}

	public void setNumberAddress(String numberAddress) {
		this.numberAddress = numberAddress;
	}

	public String getCityAddress() {
		return cityAddress;
	}

	public void setCityAddress(String cityAddress) {
		this.cityAddress = cityAddress;
	}

	public String getCountryAddress() {
		return countryAddress;
	}

	public void setCountryAddress(String countryAddress) {
		this.countryAddress = countryAddress;
	}
}
