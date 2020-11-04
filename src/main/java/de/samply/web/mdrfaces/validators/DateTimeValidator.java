package de.samply.web.mdrfaces.validators;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.EnumValidationType;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.jsf.JsfUtils;
import de.samply.web.enums.EnumDateFormat;
import de.samply.web.enums.EnumTimeFormat;
import de.samply.web.mdrfaces.MdrContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator for date and time inputs.
 *
 * @author diogo
 */
@FacesValidator("DateTimeValidator")
public class DateTimeValidator implements Validator {

  /**
   * Logging instance for this class.
   */
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * MDR client instance.
   */
  private MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

  /**
   * Get the {@link DateTimeFormats} from validation data from the MDR.
   *
   * @param validationData validation data obtained from the MDR
   *                       (e.g. "ISO_8601_WITH_DAYS;LOCAL_TIME_WITH_SECONDS")
   * @return the date and the time formats, separately
   */
  public static DateTimeFormats getDateTimeFormats(final String validationData) {
    DateTimeFormats dateTimeFormats = new DateTimeFormats(EnumDateFormat.ISO_8601_WITH_DAYS,
        EnumTimeFormat.HOURS_24);
    String[] mdrFormats = validationData.split(";");
    if (mdrFormats.length == 2) {
      EnumDateFormat enumDateFormat = EnumDateFormat.valueOf(mdrFormats[0]);
      EnumTimeFormat enumTimeFormat = EnumTimeFormat.valueOf(mdrFormats[1]);
      dateTimeFormats = new DateTimeFormats(enumDateFormat, enumTimeFormat);
    }
    return dateTimeFormats;
  }

  /**
   * Get the date/time format pattern from the MDR date format description that the date/time picker
   * can understand.
   *
   * @param enumDateFormat the date format as known in MDR
   * @param enumTimeFormat the time format as known in MDR
   * @return the date/time format pattern string representation that the date/time picker can
   *        understand
   */
  public static String getDatetimepickerPattern(final EnumDateFormat enumDateFormat,
      final EnumTimeFormat enumTimeFormat) {
    String datePattern = getDateTimePattern(enumDateFormat, enumTimeFormat);
    // Java gives yyyy-MM-dd HH:mm:ss, picker needs YYYY-MM-DD HH:mm:ss
    datePattern = datePattern.replace("y", "Y");
    datePattern = datePattern.replace("d", "D");
    return datePattern;
  }

  /**
   * Get the date format pattern from the MDR date format description.
   *
   * @param enumDateFormat the date format as known in MDR
   * @param enumTimeFormat the time format as known in the MDR
   * @return the date format pattern string representation
   */
  public static String getDateTimePattern(final EnumDateFormat enumDateFormat,
      final EnumTimeFormat enumTimeFormat) {
    String datePattern = DateValidator.getDatePattern(enumDateFormat);
    String timePattern = TimeValidator.getTimePattern(enumTimeFormat);

    if (!datePattern.isEmpty() && !timePattern.isEmpty()) {
      DateFormat formatter = new SimpleDateFormat(datePattern + " " + timePattern);
      return ((SimpleDateFormat) formatter).toPattern();
    } else {
      LoggerFactory.getLogger(DateTimeValidator.class.getName()).warn(
          "Could not get a formatter for " + enumDateFormat.name() + " " + enumTimeFormat.name());
      return "";
    }
  }

  @Override
  public final void validate(final FacesContext context, final UIComponent component,
      final Object value) throws ValidatorException {
    String mdrId = (String) component.getAttributes().get("mdrId");

    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementValidations.getValidationType().equals(EnumValidationType.DATETIME.name())) {
        // get the enum date and time format from the mdr data element
        DateTimeFormats dateTimeFormats =
            getDateTimeFormats(dataElementValidations.getValidationData());

        // translate it to a pattern
        String dateTimePattern = getDateTimePattern(dateTimeFormats.getDateFormat(),
            dateTimeFormats.getTimeFormat());
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimePattern);
        sdf.setLenient(false);

        try {
          // if not valid, it will throw ParseException
          Date date = sdf.parse(value.toString());
          logger.debug("Correctly parsed date: " + date);
        } catch (ParseException e) {
          FacesMessage msg = new FacesMessage(dataElementValidations.getErrorMessages().get(0)
              .getDesignation(), dataElementValidations.getErrorMessages().get(0).getDefinition());
          msg.setSeverity(FacesMessage.SEVERITY_ERROR);
          throw new ValidatorException(msg);
        }
      }

    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Could not get the data elements validation for mdrId " + mdrId);
    }
  }

  /**
   * Definition of a data time format broken into date and time formats.
   *
   * @author diogo
   */
  public static class DateTimeFormats {

    /**
     * The date format.
     */
    private EnumDateFormat dateFormat;
    /**
     * The time format.
     */
    private EnumTimeFormat timeFormat;

    /**
     * Create an instance with the given date and time format.
     *
     * @param dateFormat the date format.
     * @param timeFormat the time format.
     */
    public DateTimeFormats(final EnumDateFormat dateFormat, final EnumTimeFormat timeFormat) {
      super();
      this.dateFormat = dateFormat;
      this.timeFormat = timeFormat;
    }

    /**
     * Get the date format of this validator.
     *
     * @return the date format
     */
    public final EnumDateFormat getDateFormat() {
      return dateFormat;
    }

    /**
     * Set the date format of this validator.
     *
     * @param dateFormat the date format to set
     */
    public final void setDateFormat(final EnumDateFormat dateFormat) {
      this.dateFormat = dateFormat;
    }

    /**
     * Get the time format of this validator.
     *
     * @return the time format
     */
    public final EnumTimeFormat getTimeFormat() {
      return timeFormat;
    }

    /**
     * Set the time format of this validator.
     *
     * @param timeFormat the time format to set
     */
    public final void setTimeFormat(final EnumTimeFormat timeFormat) {
      this.timeFormat = timeFormat;
    }

  }
}
