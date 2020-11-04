
package de.samply.web.enums;

/**
 * Time formats as known in the MDR.
 *
 * @author diogo
 *
 */
public enum EnumTimeFormat {
  /**
   * Time defined in the session locale format.
   */
  LOCAL_TIME,
  /**
   * Time in the 24h format.
   */
  HOURS_24,
  /**
   * Time in the 12h format.
   */
  HOURS_12,
  /**
   * Time defined in the session locale format, with seconds.
   */
  LOCAL_TIME_WITH_SECONDS,
  /**
   * Time in the 24h format, with seconds.
   */
  HOURS_24_WITH_SECONDS,
  /**
   * Time in the 12h format, with seconds.
   */
  HOURS_12_WITH_SECONDS;
}
