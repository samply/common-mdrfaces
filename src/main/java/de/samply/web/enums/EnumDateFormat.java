
package de.samply.web.enums;

/**
 * Date format types, as known from MDR.
 *
 * @author diogo
 *
 */
public enum EnumDateFormat {
  /**
   * A date defined in the session locale format.
   */
  LOCAL_DATE,
  /**
   * ISO 8601 date.
   */
  ISO_8601,
  /**
   * DIN 5008 date.
   */
  DIN_5008,
  /**
   * A date defined in the session locale format, with days.
   */
  LOCAL_DATE_WITH_DAYS,
  /**
   * ISO 8601 date, with days.
   */
  ISO_8601_WITH_DAYS,
  /**
   * DIN 5008 date, with days.
   */
  DIN_5008_WITH_DAYS,
  /**
   * DIN 5008 date, only year.
   */
  DIN_5008_ONLY_YEAR;
}
