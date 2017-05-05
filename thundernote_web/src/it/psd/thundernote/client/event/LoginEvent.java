package it.psd.thundernote.client.event;

import it.psd.thundernote.shared.UserAccountDTO;
import com.google.gwt.event.shared.GwtEvent;

public class LoginEvent extends GwtEvent<LoginEventHandler> {
	  public static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
	  private final UserAccountDTO user;

	  public LoginEvent(UserAccountDTO user) {
	    this.user = user;
	  }

	  public UserAccountDTO getUser() {
	    return user;
	  }

	  @Override public Type<LoginEventHandler> getAssociatedType() {
	    return TYPE;
	  }

	  @Override protected void dispatch(LoginEventHandler handler) {
	    handler.onLogin(this);
	  }
	}
