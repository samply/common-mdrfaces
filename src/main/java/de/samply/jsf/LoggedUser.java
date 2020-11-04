
package de.samply.jsf;

import java.io.Serializable;

/**
 * Relevant information for the user session.
 *
 * @author diogo
 *
 */
public class LoggedUser implements Serializable {

  /**
   * Serial version Id.
   */
  private static final long serialVersionUID = -7236432275860137728L;

  /**
   * Local user ID.
   */
  private int id;

  /**
   * The user name.
   */
  private String name;

  /**
   * identifier of the user in the central authentications server.
   */
  private String authId;

  /**
   * The user email.
   */
  private String email;

  /**
   * the access token - used for REST calls authentication and to identify temporarily the user.
   */
  private String accessToken;

  /**
   * Instantiate a logged user.
   */
  public LoggedUser() {
  }

  /**
   * Get the local user id.
   * @return the id
   */
  public final int getId() {
    return id;
  }

  /**
   * Set the local user id.
   * @param id
   *            the id to set
   */
  public final void setId(final int id) {
    this.id = id;
  }

  /**
   * Get the user name.
   * @return the name
   */
  public final String getName() {
    return name;
  }

  /**
   * Set the user name.
   * @param name
   *            the name to set
   */
  public final void setName(final String name) {
    this.name = name;
  }

  /**
   * Get the user's id in the central authentications server.
   * @return the authId
   */
  public final String getAuthId() {
    return authId;
  }

  /**
   * Set the user's id in the central authentications server.
   * @param authId
   *            the authId to set
   */
  public final void setAuthId(final String authId) {
    this.authId = authId;
  }

  /**
   * Get the user's email address.
   * @return the email
   */
  public final String getEmail() {
    return email;
  }

  /**
   * Set the user's email address.
   * @param email
   *            the email to set
   */
  public final void setEmail(final String email) {
    this.email = email;
  }

  /**
   * Get the access token used for REST calls authentication and to identify temporarily the user.
   * @return the accessToken
   */
  public final String getAccessToken() {
    return accessToken;
  }

  /**
   * Set the access token used for REST calls authentication and to identify temporarily the user.
   * @param accessToken
   *            the accessToken to set
   */
  public final void setAccessToken(final String accessToken) {
    this.accessToken = accessToken;
  }

}
