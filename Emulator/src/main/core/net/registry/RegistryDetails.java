package core.net.registry;

import java.sql.Date;

public class RegistryDetails {

	private final String username;

	private final String password;

	private final Date birth;

	private final int country;

	public RegistryDetails(String username, String password, Date birth, int country) {
		this.username = username;
		this.password = password;
		this.birth = birth;
		this.country = country;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Date getBirth() {
		return birth;
	}

	public int getCountry() {
		return country;
	}

}
