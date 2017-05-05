package it.psd.thundernote.server;

import javax.servlet.http.HttpSession;

import it.psd.thundernote.client.LoginService;
import it.psd.thundernote.server.domain.UserAccount;
import it.psd.thundernote.shared.NotLoggedInException;
import it.psd.thundernote.shared.UserAccountDTO;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {
	
	@Override
	public UserAccountDTO getLoggedInUserDTO() {
		// TODO Auto-generated method stub
		UserAccountDTO userDTO;
	    HttpSession session = getThreadLocalRequest().getSession();
	    
	    UserAccount u = LoginHelper.getLoggedInUser(session, null);
	    if (u == null)
	        return null;
	      userDTO = UserAccount.toDTO(u);
		return userDTO;
	}
	
	@Override
	  public void logout() throws NotLoggedInException {
	    getThreadLocalRequest().getSession().invalidate();
	    throw new NotLoggedInException("Logged out");
	  }
}
