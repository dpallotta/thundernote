package it.psd.thundernote.server.servlets;

import it.psd.thundernote.server.LoginHelper;
import it.psd.thundernote.server.PMF;
import it.psd.thundernote.server.domain.Place;
import it.psd.thundernote.server.domain.UserAccount;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;

public class GetPlacesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GetPlacesServlet.class.getName());

	public GetPlacesServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PersistenceManager pm = PMF.getPm();
		UserAccount currentUser = LoginHelper.getLoggedInUser(request.getSession(), pm);
		JSONObject resp = new JSONObject();

		Set<Key> keys = currentUser.getPlaces();
		List<Object> ids = new ArrayList<Object>();
		for (Key key : keys) {
			ids.add(pm.newObjectIdInstance(Place.class, key));
		}

		if(ids.isEmpty()){
			JSONArray names = new JSONArray();
			JSONArray addresses = new JSONArray();
			JSONArray venuesId = new JSONArray();
			
			try {
				resp.put("state", "empty");
				resp.put("names", names);
				resp.put("addresses", addresses);
				resp.put("venuesId", venuesId);
			} catch (JSONException e) {
				logger.warning(e.getMessage());
			}
		}else{

			List<Place> favoritePlaces = null;
			try{
				@SuppressWarnings("unchecked")
				List<Place> results = (List<Place>) pm.getObjectsById(ids);
				favoritePlaces = results;
			}catch(JDOObjectNotFoundException onfe){
				logger.info(onfe.getMessage());
				currentUser.removePlace((Key)onfe.getFailedObject());
				pm.makePersistent(currentUser);
			}catch(JDOUserException onfe){
				logger.info(onfe.getMessage());
			}finally{
				pm.close();
			}

			JSONArray names = new JSONArray();
			JSONArray addresses = new JSONArray();
			JSONArray venuesId = new JSONArray();
			for(Place p : favoritePlaces){
				names.put(p.getName());
				addresses.put(p.getAddress());
				venuesId.put(p.getVenueId());
			}

			try {
				resp.put("state", "ok");
				resp.put("names", names);
				resp.put("addresses", addresses);
				resp.put("venuesId", venuesId);
			} catch (JSONException e) {
				logger.warning(e.getMessage());
			}

		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(resp);
		out.flush();
		out.close();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
	}
}
