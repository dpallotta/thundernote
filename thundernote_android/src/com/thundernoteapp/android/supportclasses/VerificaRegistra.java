package com.thundernoteapp.android.supportclasses;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import com.thundernoteapp.android.LoginActivity;


public class VerificaRegistra {

	public static String VerificaoRegistraUtente( String id, String name, String provider ) throws ParseException, IOException{

		String str = "";
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("loginId", id));
		params.add(new BasicNameValuePair("provider", provider));
		String paramString = URLEncodedUtils.format(params, "utf-8");
		HttpClient hc = new DefaultHttpClient();


		HttpGet get = new HttpGet(
				"http://thunder-note.appspot.com/loginmobile?method=loginStarts&" + paramString);

		HttpResponse rp = hc.execute(get, LoginActivity.httpContext);

		if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			str = EntityUtils.toString(rp.getEntity());
			str = str.replaceAll("\r\n|\r|\n", " ");
			str = str.trim();
		}

		return str;

	}
}