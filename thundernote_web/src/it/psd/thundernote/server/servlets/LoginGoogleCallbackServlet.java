package it.psd.thundernote.server.servlets;

import it.psd.thundernote.server.LoginHelper;
import it.psd.thundernote.server.domain.UserAccount;
import it.psd.thundernote.server.utils.AuthenticationProvider;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



@SuppressWarnings("serial") 
public class LoginGoogleCallbackServlet extends HttpServlet {
  private static Logger log = Logger.getLogger(LoginGoogleCallbackServlet.class
      .getName());

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    Principal googleUser = request.getUserPrincipal();
    if (googleUser != null) {
      // update or create user
      UserAccount u = new UserAccount(googleUser.getName(),  AuthenticationProvider.GOOGLE);
      u.setName(googleUser.getName());
      UserAccount thundernote = new LoginHelper().loginStarts(request.getSession(), u);
  
      log.info("User id:" + thundernote.getKey().toString() + " " + request.getUserPrincipal().getName());
    }
    response.sendRedirect(LoginHelper.getApplitionURL(request));
  }
}
