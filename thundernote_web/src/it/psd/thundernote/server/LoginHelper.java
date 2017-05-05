/** 
 * Copyright 2010 Daniel Guermeur and Amy Unruh
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   See http://connectrapp.appspot.com/ for a demo, and links to more information 
 *   about this app and the book that it accompanies.
 */
package it.psd.thundernote.server;

import it.psd.thundernote.server.domain.UserAccount;
import it.psd.thundernote.server.utils.ServletHelper;
import it.psd.thundernote.server.utils.ServletUtils;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginHelper extends RemoteServiceServlet {
	private static final long serialVersionUID = 2888983680310646846L;

	private static Logger logger = Logger.getLogger(LoginHelper.class.getName());


	static public String getApplitionURL(HttpServletRequest request) {

		if (ServletHelper.isDevelopment(request)) {
			return "http://127.0.0.1:8888/Thundernote.html?gwt.codesvr=127.0.0.1:9997";
		} else {
			return ServletUtils.getBaseUrl(request);
		}

	}

	static public UserAccount getLoggedInUser(HttpSession session, PersistenceManager pm) {

		boolean localPM = false;

		if (session == null)
			return null; // user not logged in

		String userId = (String) session.getAttribute("userId");
		if (userId == null)
			return null; // user not logged in

		if (pm == null) {
			// then create local pm
			pm = PMF.getPm();
			localPM = true;
		}

		Key key = KeyFactory.createKey(UserAccount.class.getSimpleName(), userId);

		try {
			UserAccount u = pm.getObjectById(UserAccount.class, key);
			return u;
		} finally {
			if (localPM) {
				pm.close();
			}
		}

	}

	static public boolean isLoggedIn(HttpServletRequest req) {

		if (req == null)
			return false;
		else {
			HttpSession session = req.getSession();
			if (session == null) {
				logger.info("Session is null...");
				return false;
			} else {
				Boolean isLoggedIn = (Boolean) session.getAttribute("loggedin");
				if(isLoggedIn == null){
					logger.info("Session found, but did not find loggedin attribute in it: user not logged in");
					return false;
				} else if (isLoggedIn){
					logger.info("User is logged in...");
					return true;
				} else {
					logger.info("User not logged in");
					return false;
				}
			}
		}
	}

	public UserAccount loginStarts(HttpSession session, UserAccount user) {
		UserAccount aUser = UserAccount.findOrCreateUser(user);
		
		session.setAttribute("userId", aUser.getUniqueId());
		session.setAttribute("loggedin", true);

		return aUser;
	}

}
