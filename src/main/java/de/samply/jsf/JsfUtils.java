package de.samply.jsf;

import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Frequently called methods that use JSF functionality.
 *
 * @author diogo
 */
public final class JsfUtils {

  /**
   * User session name - it is shared between mdrfaces and projects that use this component.
   */
  public static final String SESSION_USER = "samply_session_user";

  /**
   * Prevent instantiation - utility class.
   */
  private JsfUtils() {
  }

  /**
   * Extract a JSF attribute from the JSF current context. e.g. attribute defined in xhtml as
   * composite:attribute name="mdrId" required="true"
   *
   * @param attribute the attribute to extract e.g. mdrId
   * @return the value of the attribute
   */
  public static String getJsfAttribute(final String attribute) {
    FacesContext fc = FacesContext.getCurrentInstance();
    String attributeValue = fc.getApplication()
        .evaluateExpressionGet(fc, "#{cc.attrs." + attribute + "}",
            String.class);
    return attributeValue;
  }

  /**
   * Get the logged in user.
   *
   * @return the logged in user
   */
  public static LoggedUser getLoggedUser() {
    // check the session
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
        .getExternalContext()
        .getRequest();
    return (LoggedUser) request.getSession().getAttribute(SESSION_USER);
  }

  /**
   * Get the access token from the logged in user.
   *
   * @return the access token if there is a user logged in, null otherwise.
   */
  public static String getAccessToken() {
    LoggedUser loggedUser = getLoggedUser();
    return loggedUser != null ? loggedUser.getAccessToken() : null;
  }

  /**
   * Get the user authentication server ID from the logged in user.
   *
   * @return the user authentication server ID if there is a user logged in, null otherwise.
   */
  public static String getUserAuthId() {
    LoggedUser loggedUser = getLoggedUser();
    return loggedUser != null ? loggedUser.getAuthId() : null;
  }

  /**
   * Gets the locale of the JSF application.
   *
   * @return the defined locale
   */
  public static Locale getLocale() {
    if (FacesContext.getCurrentInstance() != null) {
      return FacesContext.getCurrentInstance().getViewRoot().getLocale();
    } else {
      return new Locale("de");
    }
  }

  /**
   * Gets the locale of the JSF application.
   *
   * @return the defined locale
   */
  public static String getLocaleLanguage() {
    return getLocale().getLanguage();
  }

  /**
   * Gets the display language of the JSF application.
   *
   * @return the defined display language (e.g. "English")
   */
  public static String getDisplayLanguage() {
    return getLocale().getDisplayLanguage();
  }
}
