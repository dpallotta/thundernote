package it.psd.thundernote.server.servlets;

import it.psd.thundernote.server.LoginHelper;
import it.psd.thundernote.server.PMF;
import it.psd.thundernote.server.domain.Place;
import it.psd.thundernote.server.domain.UserAccount;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.jdo.JDOCanRetryException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PlaceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PlaceServlet.class.getName());
	private static final int NUM_RETRIES = 5;

	public PlaceServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PersistenceManager pm = PMF.getPm();
		String name = request.getParameter("placeName");
		String address = request.getParameter("placeAddress");
		String venueId = request.getParameter("placeVenueId");
		Place place = Place.findOrCreatePlace(new Place(name, address, venueId));
		String result = "";
		
		try {
			// do this operation under transactional control
			for (int i = 0; i < NUM_RETRIES; i++) {
				pm.currentTransaction().begin();

				UserAccount currentUser = LoginHelper.getLoggedInUser(request.getSession(), pm);
				
				if( currentUser.addPlace(place.getKey()) ){
					pm.makePersistent(currentUser);
					result = "Place Saved!";
				}else{
					result = "Place already saved...";
				}

				try {
					logger.fine("starting commit");
					pm.currentTransaction().commit();
					logger.fine("commit was successful");
					break;
				} catch (JDOCanRetryException e1) {
					if (i == (NUM_RETRIES - 1)) {
						throw e1;
					}
				}
			}
		} catch (Exception e) {
			logger.warning(e.getMessage());
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
				logger.warning("did transaction rollback");
				result = "Error...try again";
			}
			pm.close();
		}
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(result);
		out.flush();
		out.close();
	}
}
