package it.psd.thundernote.client;

import it.psd.thundernote.shared.UserAccountDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>LoginService</code>.
 */
public interface LoginServiceAsync {
	void getLoggedInUserDTO(AsyncCallback<UserAccountDTO> callback);
	void logout(AsyncCallback<Void> callback);
}
