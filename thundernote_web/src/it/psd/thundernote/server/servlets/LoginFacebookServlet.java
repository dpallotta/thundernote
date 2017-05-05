package it.psd.thundernote.server.servlets;

import it.psd.thundernote.server.LoginHelper;
import it.psd.thundernote.server.domain.UserAccount;
import it.psd.thundernote.server.utils.AuthenticationProvider;
import it.psd.thundernote.server.utils.UrlFetcher;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFacebookServlet extends LoginSuperServlet {
  private static final long serialVersionUID = -1187933703374946249L;
  private static Logger log = Logger.getLogger(LoginFacebookServlet.class.getName());

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String callbackURL = null, clientSecret = null, appId = null;

    appId = AuthenticationProvider.getProp("fb_app_id");
    clientSecret = AuthenticationProvider.getProp("fb_secret");

    if(appId == null || clientSecret == null){
      response.setContentType("text/html");
      //response.getWriter().print(AppLib.INFONOTFOUND);
      return;
    }
    callbackURL = AuthenticationProvider.getProp("fb_callback_url");
    String code = request.getParameter("code");

    if (code != null && !code.isEmpty()) {
      // Facebook authentication has been done and Facebook is calling us back here:
      String keyNValues = getParams(request);
      log.info("==== STEP 2/1: got params=" + keyNValues);
      log.info("==== STEP 2/2: got code=" + code);

      /*
       * Save code in session
       */
      request.getSession().setAttribute("facebook_code", code);

      /*
       * Get access token
       */
      String tokenURL = "https://graph.facebook.com/oauth/access_token" + "?client_id=" + appId + "&redirect_uri="
          + callbackURL + "&client_secret=" + clientSecret + "&code=" + code;

      log.info("requesting access token url=" + tokenURL);
      String resp = UrlFetcher.get(tokenURL);
      log.info("Response = " + resp);
      int beginIndex = "access_token=".length();
      String token = resp.substring(beginIndex);
      log.info("Extracted token = " + token);

      /*
       * Get user info
       */
      String url = "https://graph.facebook.com/me?access_token=" + token;
      log.info("requesting user info: " + url);
      resp = UrlFetcher.get(url);
      log.info("Response: " + resp);
      UserAccount thundernote = extractUserInfo(resp);
      thundernote = new LoginHelper().loginStarts(request.getSession(), thundernote);
      log.info("User id is logged in:" + thundernote.getKey().toString());

      /*
       * All done. Let's go home.
       */
      response.sendRedirect(LoginHelper.getApplitionURL(request));

    } else { 
      // Redirect to Facebook login page
      log.info("Starting FB authentication appid: " + appId + " - callback: " + callbackURL);
      String fbLoginPage = "https://graph.facebook.com/oauth/authorize" 
        + "?client_id=" + appId 
        + "&redirect_uri=" + callbackURL;

      response.sendRedirect(fbLoginPage);
    }

  }


  private UserAccount extractUserInfo(String resp) {
    log.info("Extracting user info");
    JSONObject j;
    UserAccount u = null;
    try {
      j = new JSONObject(resp);
      String first = j.getString("first_name");
      String last = j.getString("last_name");
      String id = j.getString("id");
      log.info("User info from JSON: " + first + " " + last + " id = " + id);
      u = new UserAccount(id, AuthenticationProvider.FACEBOOK);
      u.setName(first + " " + last);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return u;
  }

  private String getParams(HttpServletRequest request) {
    Enumeration<?> all = request.getParameterNames();
    String msg = "";
    while (all.hasMoreElements()) {
      String key = (String) all.nextElement();
      msg += key + " = " + request.getParameter(key) + "<br/>\n";
    }
    return msg;
  }
}
