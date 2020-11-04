package de.samply.web.mdrfaces.validators;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.EnumValidationType;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.jsf.JsfUtils;
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
 * Validator for time.
 *
 * @author diogo
 */
@FacesValidator("TimeValidator")
public class TimeValidator implements Validator {

  /**
   * Logging instance for this class.
   */
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * MDR client instance.
   */
  private MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

  /**
   * Get the date format pattern from the MDR date format description that the timepicker can
   * understand.
   *
   * @param enumTimeFormat the date format as known in MDR
   * @return the date format pattern string representation that the timepicker understands
   */
  public static String getTimepickerPattern(final EnumTimeFormat enumTimeFormat) {
    String datePattern = getTimePattern(enumTimeFormat);
    return datePattern.toUpperCase();
  }

  /**
   * Get the date format pattern from the MDR date format description.
   *
   * @param enumTimeFormat the date format as known in MDR
   * @return the date format pattern string representation
   */
  public static String getTimePattern(final EnumTimeFormat enumTimeFormat) {
    DateFormat formatter;
    switch (enumTimeFormat) {
      case HOURS_24:
        formatter = new SimpleDateFormat("HH:mm");
        break;
      case HOURS_24_WITH_SECONDS:
        formatter = new SimpleDateFormat("HH:mm:ss");
        break;
      case HOURS_12:
        formatter = new SimpleDateFormat("h:mm a");
        break;
      case HOURS_12_WITH_SECONDS:
        formatter = new SimpleDateFormat("h:mm:ss a");
        break;
      case LOCAL_TIME:
        formatter = DateFormat.getTimeInstance(DateFormat.SHORT, JsfUtils.getLocale());
        break;
      case LOCAL_TIME_WITH_SECONDS:
        formatter = DateFormat.getTimeInstance(DateFormat.MEDIUM, JsfUtils.getLocale());
        break;
      default:
        formatter = new SimpleDateFormat("HH:mm");
        break;
    }
    return ((SimpleDateFormat) formatter).toPattern();
  }

  @Override
  public final void validate(final FacesContext context, final UIComponent component,
      final Object value)
      throws ValidatorException {
    String mdrId = (String) component.getAttributes().get("mdrId");

    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementValidations.getValidationType().equals(EnumValidationType.TIME.name())) {

        // get the enum date format from the mdr data element
        EnumTimeFormat enumTimeFormat = EnumTimeFormat
            .valueOf(dataElementValidations.getValidationData());
        // translate it to a pattern
        String datePattern = getTimePattern(enumTimeFormat);
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
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
    } catch (NumberFormatException e) {
      logger.debug("Value is not a number... " + mdrId);
    }
  }
}
