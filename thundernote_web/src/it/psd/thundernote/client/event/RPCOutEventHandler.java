package it.psd.thundernote.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface RPCOutEventHandler extends EventHandler {
  void onRPCOut(RPCOutEvent event);
}
