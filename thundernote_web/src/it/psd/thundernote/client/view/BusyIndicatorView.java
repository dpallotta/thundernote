package it.psd.thundernote.client.view;


import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import it.psd.thundernote.client.presenter.BusyIndicatorPresenter;
/**
 * Displays a loading message, indicating the app is working while
 * the user is waiting.
 * 
 */
public class BusyIndicatorView extends PopupPanel implements BusyIndicatorPresenter.Display{
  private Label message = new Label("Working...");

  public BusyIndicatorView() {
    setAnimationEnabled(false);
    add(message);
  }

  public BusyIndicatorView(String msg) {
    this();
    message.setText(msg);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

}
