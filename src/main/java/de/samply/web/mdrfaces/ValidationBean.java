
package de.samply.web.mdrfaces;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.EnumDataType;
import de.samply.common.mdrclient.domain.EnumValidationType;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.jsf.JsfUtils;
import de.samply.web.enums.EnumDateFormat;
import de.samply.web.enums.EnumTimeFormat;
import de.samply.web.mdrfaces.validators.DateTimeValidator;
import de.samply.web.mdrfaces.validators.DateTimeValidator.DateTimeFormats;
import de.samply.web.mdrfaces.validators.DateValidator;
import de.samply.web.mdrfaces.validators.TimeValidator;
import java.util.concurrent.ExecutionException;
import javax.faces.bean.ManagedBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean that helps processing validations for widgets rendered based on data from the MDR.
 *
 * @author diogo
 *
 */
@ManagedBean(name = "ValidationBean")
public class ValidationBean {

  /**
   * Logging instance for this class.
   */
  private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  /**
   * MDR client instance.
   */
  private MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

  /**
   * Check whether a data type validation should be disabled for a data element.
   *
   * @param mdrId
   *            the MDR data element loaded
   * @param dataType
   *            validation data type to be checked
   * @return true if the given type validation should be disabled, false otherwise
   */
  public final boolean disableTypeValidation(final String mdrId, final EnumDataType dataType) {

    if (mdrId != null && !mdrId.isEmpty()) {
      try {
        Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
            JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());
        return !dataElementValidations.getDatatype().equals(dataType.name());
      } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
        logger.warn("Could not : " + e.getMessage());
      }
    }
    return true;
  }

  /**
   * Check whether the integer validation should be disabled for a data element.
   *
   * @param mdrId
   *            the MDR data element loaded
   * @return true if the integer validation should be disabled, false otherwise
   */
  public final boolean disableIntegerValidation(final String mdrId) {
    return disableTypeValidation(mdrId, EnumDataType.INTEGER);
  }

  /**
   * Check whether the float validation should be disabled for a data element.
   *
   * @param mdrId
   *            the MDR data element loaded
   * @return true if the float validation should be disabled, false otherwise
   */
  public final boolean disableFloatValidation(final String mdrId) {
    return disableTypeValidation(mdrId, EnumDataType.FLOAT);
  }

  /**
   * Check whether the regex validation should be disabled for a data element.
   *
   * @param mdrId
   *            the MDR data element loaded
   * @return true if the regex validation should be disabled, false otherwise
   */
  public final boolean disableRegexValidation(final String mdrId) {
    return disableTypeValidation(mdrId, EnumDataType.STRING);
  }

  /**
   * Get the date format for an MDR data element.
   *
   * @param mdrId
   *            the MDR data element loaded
   * @return the date format pattern for the given data element
   */
  public final String getDateFormat(final String mdrId) {
    String datePattern = "";
    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementValidations.getValidationType().equals(EnumValidationType.DATE.name())) {
        EnumDateFormat enumDateFormat =
            EnumDateFormat.valueOf(dataElementValidations.getValidationData());
        datePattern = DateValidator.getDatepickerPattern(enumDateFormat);
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.warn("Could not get the date format pattern for mdrId: " + mdrId
          + " | message: " + e.getMessage());
    }
    return datePattern;
  }

  /**
   * Get the language to be shown in the Datetime widget. Currently it is the user locale.
   *
   * @return the locale to be used in the Datetime widget (e.g. "de")
   */
  public final String getDatetimeLocale() {
    return JsfUtils.getLocaleLanguage();
  }

  /**
   * Get the date time/format for a MDR data element.
   *
   * @param mdrId
   *            the MDR data element loaded
   * @return the date/time format pattern for a date
   */
  public final String getDatetimeFormat(final String mdrId) {
    String dateTimePattern = "";
    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementValidations.getValidationType().equals(EnumValidationType.DATETIME.name())) {
        DateTimeFormats dateTimeFormats =
            DateTimeValidator.getDateTimeFormats(dataElementValidations.getValidationData());
        dateTimePattern = DateTimeValidator.getDatetimepickerPattern(dateTimeFormats
                .getDateFormat(), dateTimeFormats.getTimeFormat());
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.warn("Could not get the date format pattern for mdrId: " + mdrId
          + " | message: " + e.getMessage());
    }
    return dateTimePattern;
  }

  /**
   * Get the date time for a MDR data element.
   *
   * @param mdrId
   *            the MDR data element loaded
   * @return the time format pattern for a date
   */
  public final String getTimeFormat(final String mdrId) {
    String timeFormat = "";
    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementValidations.getValidationType().equals(EnumValidationType.TIME.name())) {
        EnumTimeFormat enumTimeFormat =
            EnumTimeFormat.valueOf(dataElementValidations.getValidationData());
        timeFormat = TimeValidator.getTimePattern(enumTimeFormat);
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.warn("Could not get the date format pattern for mdrId: " + mdrId
          + " | message: " + e.getMessage());
    }
    return timeFormat;
  }

  /**
   * Check whether the seconds option should be presented in the time format.
   *
   * @param mdrId
   *            the MDR data element loaded
   * @return true if the seconds option should be presented in the time format, false otherwise
   */
  public final String showSeconds(final String mdrId) {
    boolean showSeconds = false;

    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());
      EnumTimeFormat enumTime = EnumTimeFormat.HOURS_24; // default
      if (dataElementValidations.getValidationType().equals(EnumValidationType.DATETIME.name())) {
        DateTimeFormats dateTimeFormats =
            DateTimeValidator.getDateTimeFormats(dataElementValidations.getValidationData());
        enumTime = dateTimeFormats.getTimeFormat();
      } else if (dataElementValidations.getValidationType()
          .equals(EnumValidationType.TIME.name())) {
        enumTime = EnumTimeFormat.valueOf(dataElementValidations.getValidationData());
      }

      if (enumTime.equals(EnumTimeFormat.HOURS_12_WITH_SECONDS)
          || enumTime.equals(EnumTimeFormat.HOURS_24_WITH_SECONDS)
          || enumTime.equals(EnumTimeFormat.LOCAL_TIME_WITH_SECONDS)) {
        showSeconds = true;
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.warn("Could not get the date format pattern for mdrId: " + mdrId
          + " | message: " + e.getMessage());
    }

    return String.valueOf(showSeconds);
  }
}
