package it.psd.thundernote.client;

import it.psd.thundernote.shared.NotLoggedInException;
import it.psd.thundernote.shared.UserAccountDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("loginService")
public interface LoginService extends RemoteService {
	public UserAccountDTO getLoggedInUserDTO();
	void logout() throws NotLoggedInException;
}
