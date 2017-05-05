package it.psd.thundernote.client;

import it.psd.thundernote.client.event.LoginEvent;
import it.psd.thundernote.client.event.LoginEventHandler;
import it.psd.thundernote.client.event.LogoutEvent;
import it.psd.thundernote.client.event.LogoutEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

public class AppController implements ValueChangeHandler<String>{

	private final SimpleEventBus eventBus;

	public AppController(SimpleEventBus eventBus) {
		this.eventBus = eventBus;
		bind();
	}
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		if(token!=null){
			if(token.equals("places")){
				Window.Location.assign("/places.jsp");
				return;
			}
			else if (token.equals("login")) {
				Thundernote.get().showLoginView();
				return;

			}
		}
	}

	private void bind(){
		History.addValueChangeHandler(this);
		
		eventBus.addHandler(LogoutEvent.TYPE, new LogoutEventHandler() {
			@Override public void onLogout(LogoutEvent event) {
				GWT.log("AppController: Logout event received");
				doLogout();
			}
		});
		
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override public void onLogin(LoginEvent event) {
				GWT.log("AppController: Login event received");
				go();
			}
		});
	}

	private void doLogout() {
		History.newItem("login");
	}

	public void go() {

		if ("".equals(History.getToken()) || "_=_".equals(History.getToken())) {
			History.newItem("places");
		} else {
			History.fireCurrentHistoryState();
		}
	}

}
