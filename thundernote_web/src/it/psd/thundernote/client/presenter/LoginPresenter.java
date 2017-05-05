package it.psd.thundernote.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class LoginPresenter {

	public interface Display {
		HasClickHandlers getGoogleButton();

		HasClickHandlers getTwitterButton();

		HasClickHandlers getFacebookButton();

		Widget asWidget();
	}

	private final Display display;


	public LoginPresenter(SimpleEventBus eventBus, Display display) {
		this.display = display;
	}

	public void bind() {

		this.display.getGoogleButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doLoginGoogle();
			}
		});

		this.display.getTwitterButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doLoginTwitter();
			}
		});

		this.display.getFacebookButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doLoginFacebook();
			}
		});

	}

	public void go(final HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
		bind();
	}

	private void doLoginFacebook() {
		Window.Location.assign("/loginfacebook");
	}

	private void doLoginGoogle() {
		Window.Location.assign("/logingoogle");
	}

	private void doLoginTwitter() {
		Window.Location.assign("/logintwitter");
	}
}
