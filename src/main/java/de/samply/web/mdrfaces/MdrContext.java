package de.samply.web.mdrfaces;

import de.samply.common.mdrclient.MdrClient;

/**
 * Context of the MDR connection. This instance needs to be initialized before MdrFaces is used.
 *
 * @author diogo
 *
 */
public enum MdrContext {
  /**
   * Only instance of the MDR Context.
   */
  INSTANCE;

  /**
   * The MDR client instance.
   */
  private MdrClient mdrClient;

  /**
   * Initialize MdrFaces with the desired MdrClient instance.
   *
   * @param mdrClient
   *            MDR client instance
   */
  public void init(final MdrClient mdrClient) {
    this.mdrClient = mdrClient;
  }

  /**
   * Get the MDR context.
   *
   * @return the MDR context
   */
  public static MdrContext getMdrContext() {
    return INSTANCE;
  }

  /**
   * Get the mdrClient instance used to initialize the MDR context.
   *
   * @return the mdrClient instance used to initialize the MDR context.
   * @throws IllegalStateException
   *             if the MDR context has not been initialized.
   */
  public MdrClient getMdrClient() throws IllegalStateException {
    if (mdrClient != null) {
      return mdrClient;
    } else {
      throw new IllegalStateException("The MDR context has not been initialised before MdrFaces "
          + "usage.");
    }
  }
}
