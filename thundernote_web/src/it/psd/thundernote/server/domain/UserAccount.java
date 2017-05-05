package it.psd.thundernote.server.domain;

import it.psd.thundernote.server.PMF;
import it.psd.thundernote.shared.UserAccountDTO;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.JDOCanRetryException;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class UserAccount implements Serializable{

	private static final Logger log = Logger.getLogger(UserAccount.class.getName());
	private static final int NUM_RETRIES = 5;


	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String name;

	@Persistent
	private String emailAddress;

	/**
	 * loginId and loginProvider form a unique key. 
	 * E.g.: loginId = supercobra, loginProvider = LoginProvider.TWITTER
	 */
	@Persistent
	private String uniqueId;
	
	//pointer back to places
	@Persistent
	private Set<Key> favoritePlaces = new HashSet<Key>();

	public UserAccount() {
	}

	public UserAccount(String loginId, Integer loginProvider) {
		this();
		this.setName(loginId);
		this.setUniqueId(loginId + "-" + loginProvider);
	}
	
	public static UserAccount findOrCreateUser(UserAccount user) {

	    PersistenceManager pm = PMF.getPm();
	    Transaction tx = null;
	    UserAccount oneResult = null, detached = null;

	    String uniqueId = user.getUniqueId();
	    Key key = KeyFactory.createKey(UserAccount.class.getSimpleName(), uniqueId);

	    // perform the query and creation under transactional control,
	    // to prevent another process from creating an account with the same id.
	    try {
	    	for (int i = 0; i < NUM_RETRIES; i++) {
	    		tx = pm.currentTransaction();
	    		tx.begin();
	    		try{
	    			oneResult = pm.getObjectById(UserAccount.class, key);
	    			log.info("User uniqueId already exists: " + uniqueId);
	    			detached = pm.detachCopy(oneResult);
	    		}
	    		catch(JDOObjectNotFoundException onfe){
	    			log.info("UserAccount " + uniqueId + " does not exist, creating...");
	    			user.setKey(key);
	    			pm.makePersistent(user);
	    			detached = pm.detachCopy(user);
	    		}

	    		try {
	    			tx.commit();
	    			break;
	    		}
	    		catch (JDOCanRetryException e1) {
	    			if (i == (NUM_RETRIES - 1)) { 
	    				throw e1;
	    			}
	    		}
	    	} // end for
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    } 
	    finally {
	    	if (tx.isActive()) {
	    		tx.rollback();
	    	}
	    	pm.close();
	    }
	    
	    return detached;
	  }
	
	public static UserAccountDTO toDTO(UserAccount user) {
	    if (user == null) {
	      return null;
	    }
	    UserAccountDTO accountDTO = new UserAccountDTO(user.getEmailAddress(), user.getName(), user.getUniqueId());
	    return accountDTO;
	  }


	public void setBasicInfo(String name, String emailAddress, String uniqueId) {
		this.name = name;
		this.emailAddress = emailAddress;
		this.uniqueId = uniqueId;
	}

	public Key getKey(){
		return key;
	}
	
	public void setKey(Key key) {
		this.key = key;
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
	
	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	public boolean addPlace(Key pl) {
		return favoritePlaces.add(pl);
	}
	
	public boolean removePlace(Key pl){
		return favoritePlaces.remove(pl);
	}

	public Set<Key> getPlaces() {
		return favoritePlaces;
	}
}
