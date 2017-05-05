package it.psd.thundernote.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

public interface GlobalResources extends ClientBundle {

	@Source("facebooklogo.jpeg")
	DataResource facebooklogo();

	ImageResource googlelogo();

	ImageResource logo();

	ImageResource thundernote();

	ImageResource twitterlogo();

}
