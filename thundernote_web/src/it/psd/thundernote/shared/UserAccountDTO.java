package it.psd.thundernote.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserAccountDTO implements Serializable {

	private String name;
	private String emailAddress;
	private String uniqueId;
	
	public UserAccountDTO() {
		  
	  }
	
	public UserAccountDTO(String emailAddress, String name, String uniqueId) {
		super();
		this.setEmailAddress(emailAddress);
		this.setName(name);
		this.setUniqueId(uniqueId);
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
