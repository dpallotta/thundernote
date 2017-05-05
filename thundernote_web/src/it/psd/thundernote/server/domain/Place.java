package it.psd.thundernote.server.domain;

import it.psd.thundernote.server.PMF;

import java.io.Serializable;
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
public class Place implements Serializable{

	private static final Logger log = Logger.getLogger(Place.class.getName());
	private static final int NUM_RETRIES = 5;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String name;
	@Persistent
	private String address;
	@Persistent
	private String venueId;

	public Place() {

	}

	public Place(String name, String address, String venueId) {
		this();
		this.setBasicInfo(name, address, venueId);
	}

	public static Place findOrCreatePlace(Place place) {

		PersistenceManager pm = PMF.getPm();
		Transaction tx = null;
		Place oneResult = null, detached = null;

		String uniqueId = place.getVenueId();
		Key key = KeyFactory.createKey(Place.class.getSimpleName(), uniqueId);

		//Transaction
		try {
	    	for (int i = 0; i < NUM_RETRIES; i++) {
	    		tx = pm.currentTransaction();
	    		tx.begin();
	    		try{
	    			oneResult = pm.getObjectById(Place.class, key);
	    			log.info("User uniqueId already exists: " + uniqueId);
	    			detached = pm.detachCopy(oneResult);
	    		}
	    		catch(JDOObjectNotFoundException onfe){
	    			log.info("UserAccount " + uniqueId + " does not exist, creating...");
	    			place.setKey(key);
	    			pm.makePersistent(place);
	    			detached = pm.detachCopy(place);
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

	public void setBasicInfo(String name, String address,
			String venueId) {

		this.setName(name);
		this.setAddress(address);
		this.setVenueId(venueId);
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key){
		this.key = key;
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

	public String getVenueId() {
		return venueId;
	}

	public void setVenueId(String venueId) {
		this.venueId = venueId;
	}
}
