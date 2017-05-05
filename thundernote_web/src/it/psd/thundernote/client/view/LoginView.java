package it.psd.thundernote.client.view;

import it.psd.thundernote.client.presenter.LoginPresenter.Display;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class LoginView extends Composite implements Display{

	private static LoginViewUiBinder uiBinder = GWT
			.create(LoginViewUiBinder.class);

	interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {
	}
	
	@UiField PushButton googleButton;
	@UiField PushButton twitterButton;
	@UiField PushButton facebookButton;

	public LoginView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public Widget asWidget() {
	    return this;
	  }

	public HasClickHandlers getFacebookButton() {
	    return facebookButton;
    }

	public HasClickHandlers getGoogleButton() {
	    return googleButton;
	}

    public HasClickHandlers getTwitterButton() {
	    return twitterButton;
    }

}
