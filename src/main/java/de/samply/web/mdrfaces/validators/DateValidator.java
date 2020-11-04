package de.samply.web.mdrfaces.validators;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.EnumValidationType;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.jsf.JsfUtils;
import de.samply.web.enums.EnumDateFormat;
import de.samply.web.mdrfaces.MdrContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
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
 * Validator for date inputs.
 *
 * @author diogo
 */
@FacesValidator("DateValidator")
public class DateValidator implements Validator {

  /**
   * Logging instance for this class.
   */
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  /**
   * MDR client instance.
   */
  private MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

  /**
   * Get the date format pattern from the MDR date format description that the date/time picker can
   * understand.
   *
   * @param enumDateFormat the date format as known in MDR
   * @return the date format pattern string representation that the date/time picker can understand
   */
  public static String getDatepickerPattern(final EnumDateFormat enumDateFormat) {
    String datePattern = getDatePattern(enumDateFormat);
    return datePattern.toUpperCase();
  }

  /**
   * Get the date format pattern from the MDR date format description.
   *
   * @param enumDateFormat the date format as known in MDR
   * @return the date format pattern string representation
   */
  public static String getDatePattern(final EnumDateFormat enumDateFormat) {
    DateFormat formatter;
    switch (enumDateFormat) {
      case DIN_5008:
        formatter = new SimpleDateFormat("MM.yyyy");
        break;
      case DIN_5008_WITH_DAYS:
        formatter = new SimpleDateFormat("dd.MM.yyyy");
        break;
      case DIN_5008_ONLY_YEAR:
        formatter = new SimpleDateFormat("yyyy");
        break;
      case ISO_8601:
        formatter = new SimpleDateFormat("yyyy-MM");
        break;
      case ISO_8601_WITH_DAYS:
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        break;
      case LOCAL_DATE: // not valid
      case LOCAL_DATE_WITH_DAYS:
      default:
        formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, JsfUtils.getLocale());
        break;
    }
    return ((SimpleDateFormat) formatter).toPattern();
  }

  @Override
  public final void validate(final FacesContext context, final UIComponent component,
      final Object value) throws ValidatorException {
    String mdrId = (String) component.getAttributes().get("mdrId");

    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementValidations.getValidationType().equals(EnumValidationType.DATE.name())) {

        // get the enum date format from the mdr data element
        EnumDateFormat enumDateFormat =
            EnumDateFormat.valueOf(dataElementValidations.getValidationData());
        // translate it to a pattern
        String datePattern = getDatePattern(enumDateFormat);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern);

        try {
          // if not valid, it will throw ParseException
          TemporalAccessor dateParsed = dtf.parse(value.toString());
          logger.debug("Correctly parsed date: " + value.toString());
        } catch (DateTimeParseException e) {
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
