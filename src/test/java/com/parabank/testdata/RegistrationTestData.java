package com.parabank.testdata;

/**
 * Simple data holder for a ParaBank registration form submission.
 * Use the {@link Builder} to construct instances, or {@link #defaultValidUser()}
 * for a quick valid record with a unique, timestamp-based username.
 */
public class RegistrationTestData {

	private final String firstName;
	private final String lastName;
	private final String address;
	private final String city;
	private final String state;
	private final String zipCode;
	private final String phoneNumber;
	private final String ssn;
	private final String username;
	private final String password;
	private final String confirmPassword;

	private RegistrationTestData(Builder b) {
		this.firstName = b.firstName;
		this.lastName = b.lastName;
		this.address = b.address;
		this.city = b.city;
		this.state = b.state;
		this.zipCode = b.zipCode;
		this.phoneNumber = b.phoneNumber;
		this.ssn = b.ssn;
		this.username = b.username;
		this.password = b.password;
		this.confirmPassword = b.confirmPassword;
	}

	public String getFirstName() { return firstName; }
	public String getLastName() { return lastName; }
	public String getAddress() { return address; }
	public String getCity() { return city; }
	public String getState() { return state; }
	public String getZipCode() { return zipCode; }
	public String getPhoneNumber() { return phoneNumber; }
	public String getSsn() { return ssn; }
	public String getUsername() { return username; }
	public String getPassword() { return password; }
	public String getConfirmPassword() { return confirmPassword; }

	@Override
	public String toString() {
		// Intentionally omit password/ssn from logs/reports.
		return "RegistrationTestData{firstName=" + firstName + ", lastName=" + lastName
				+ ", username=" + username + "}";
	}

	public static Builder builder() {
		return new Builder();
	}

	/** A ready-to-use valid record with a unique username/SSN/phone for each call. */
	public static RegistrationTestData defaultValidUser() {
		long ts = System.currentTimeMillis();
		return builder()
				.firstName("John")
				.lastName("Smith")
				.address("123 Main Street")
				.city("Springfield")
				.state("IL")
				.zipCode("62704")
				.phoneNumber("555" + String.valueOf(ts).substring(4))
				.ssn(String.valueOf(ts).substring(0, 9))
				.username("testuser" + ts)
				.password("Passw0rd!")
				.confirmPassword("Passw0rd!")
				.build();
	}

	public static class Builder {
		private String firstName = "";
		private String lastName = "";
		private String address = "";
		private String city = "";
		private String state = "";
		private String zipCode = "";
		private String phoneNumber = "";
		private String ssn = "";
		private String username = "";
		private String password = "";
		private String confirmPassword = "";

		public Builder firstName(String v) { this.firstName = v; return this; }
		public Builder lastName(String v) { this.lastName = v; return this; }
		public Builder address(String v) { this.address = v; return this; }
		public Builder city(String v) { this.city = v; return this; }
		public Builder state(String v) { this.state = v; return this; }
		public Builder zipCode(String v) { this.zipCode = v; return this; }
		public Builder phoneNumber(String v) { this.phoneNumber = v; return this; }
		public Builder ssn(String v) { this.ssn = v; return this; }
		public Builder username(String v) { this.username = v; return this; }
		public Builder password(String v) { this.password = v; return this; }
		public Builder confirmPassword(String v) { this.confirmPassword = v; return this; }

		/** Copies all fields from an existing valid record, so a single field can be overridden. */
		public Builder from(RegistrationTestData source) {
			this.firstName = source.firstName;
			this.lastName = source.lastName;
			this.address = source.address;
			this.city = source.city;
			this.state = source.state;
			this.zipCode = source.zipCode;
			this.phoneNumber = source.phoneNumber;
			this.ssn = source.ssn;
			this.username = source.username;
			this.password = source.password;
			this.confirmPassword = source.confirmPassword;
			return this;
		}

		public RegistrationTestData build() {
			return new RegistrationTestData(this);
		}
	}
}
