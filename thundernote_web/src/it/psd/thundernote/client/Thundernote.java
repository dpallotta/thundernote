package it.psd.thundernote.client;

import it.psd.thundernote.client.event.LoginEvent;
import it.psd.thundernote.client.presenter.BusyIndicatorPresenter;
import it.psd.thundernote.client.presenter.LoginPresenter;
//import it.psd.thundernote.client.presenter.UserBadgePresenter;
import it.psd.thundernote.client.view.BusyIndicatorView;
import it.psd.thundernote.client.view.LoginView;
//import it.psd.thundernote.client.view.PlacesView;
//import it.psd.thundernote.client.view.UserBadgeView;
import it.psd.thundernote.shared.UserAccountDTO;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Thundernote implements EntryPoint {
	
	private static Thundernote singleton;
	private UserAccountDTO currentUser;
	private SimpleEventBus eventBus = new SimpleEventBus();
	BusyIndicatorPresenter busyIndicator = new BusyIndicatorPresenter(eventBus, new BusyIndicatorView("Loading..."));

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);
	
	
	public static Thundernote get() {
	    return singleton;
	  }

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		singleton = this;
		new AppController(eventBus);
		getLoggedInUser();
		
	}
	
	private void getLoggedInUser(){
		new RPCCall<UserAccountDTO>() {
		      @Override protected void callService(AsyncCallback<UserAccountDTO> cb) {
		        loginService.getLoggedInUserDTO(cb);
		      }

		      @Override public void onSuccess(UserAccountDTO loggedInUserDTO) {
		        if (loggedInUserDTO == null) {
		          // nobody is logged in
		          showLoginView();
		        } else {
		          // user is logged in
		          setCurrentUser(loggedInUserDTO);
		          createUI();
		        }
		      }

		      @Override public void onFailure(Throwable caught) {
		        Window.alert("Error: " + caught.getMessage());
		      }
		    }.retry(3);
	}
	
	public void showLoginView(){
		RootPanel.get().clear();
		
		LoginPresenter loginPresenter = new LoginPresenter(eventBus, new LoginView());
		loginPresenter.go(RootPanel.get());
	}
	
	private void createUI(){
		GWT.runAsync(new RunAsyncCallback() {
			@Override public void onFailure(Throwable reason) {
				Window.alert("Code download error: " + reason.getMessage());
			}

			@Override public void onSuccess() {
				eventBus.fireEvent(new LoginEvent(currentUser));
			}
		});
	}

	public SimpleEventBus getEventBus() {
		return eventBus;
	}

	void setCurrentUser(UserAccountDTO currentUser) {
		this.currentUser = currentUser;
	}

	public UserAccountDTO getCurrentUser() {
		return currentUser;
	}
}
