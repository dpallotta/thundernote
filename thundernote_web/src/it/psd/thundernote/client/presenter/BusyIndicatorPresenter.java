package it.psd.thundernote.client.presenter;

import it.psd.thundernote.client.event.RPCInEvent;
import it.psd.thundernote.client.event.RPCInEventHandler;
import it.psd.thundernote.client.event.RPCOutEvent;
import it.psd.thundernote.client.event.RPCOutEventHandler;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class BusyIndicatorPresenter {

  public interface Display {
    void show();
    void hide();
    Widget asWidget();
  }

  int outCount = 0; // # of RPC calls sent by the app. If > 0 we'll show a
            // 'busy' indicator.

  private final SimpleEventBus eventBus;
  private Display display;

  public BusyIndicatorPresenter(SimpleEventBus eventBus, Display view) {
    this.eventBus = eventBus;
    this.display = view;

    bind();
  }

  public void bind() {
    eventBus.addHandler(RPCInEvent.TYPE, new RPCInEventHandler() {
      @Override
      public void onRPCIn(RPCInEvent event) {
        outCount = outCount > 0 ? --outCount : 0;
        if (outCount <= 0) {
          display.hide();
        }
      }
    });
    eventBus.addHandler(RPCOutEvent.TYPE, new RPCOutEventHandler() {
      @Override
      public void onRPCOut(RPCOutEvent event) {
        outCount++;
        display.show();
      }
    });
  }

  public void go(HasWidgets container) {
    // nothing to do
  }
}
