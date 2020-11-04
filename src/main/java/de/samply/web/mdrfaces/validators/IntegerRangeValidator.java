package de.samply.web.mdrfaces.validators;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.EnumValidationType;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.jsf.JsfUtils;
import de.samply.web.mdrfaces.MdrContext;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator for integers between a maximum and a minimum value.
 *
 * @author diogo
 */
@FacesValidator("IntegerRangeValidator")
public class IntegerRangeValidator implements Validator {

  /**
   * Logging instance for this class.
   */
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * MDR client instance.
   */
  private MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

  @Override
  public final void validate(final FacesContext context, final UIComponent component,
      final Object value)
      throws ValidatorException {
    String mdrId = (String) component.getAttributes().get("mdrId");

    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementValidations.getValidationType()
          .equals(EnumValidationType.INTEGERRANGE.name())) {
        // check if it is a valid range - range definition is the same for floats and integers
        Pattern pattern = Pattern.compile(FloatRangeValidator.FLOAT_RANGE_REGEX);
        Matcher matcher = pattern.matcher(dataElementValidations.getValidationData());

        if (matcher.find()) {
          String min = matcher.group(1);
          String max = matcher.group(2);

          Integer minInteger = Integer.valueOf(min);
          Integer maxInteger = Integer.valueOf(max);
          Integer integerValue = Integer.valueOf(String.valueOf(value));

          if (minInteger != null && integerValue < minInteger || maxInteger != null
              && integerValue > maxInteger) {
            FacesMessage msg = new FacesMessage(dataElementValidations.getErrorMessages().get(0)
                .getDesignation(),
                dataElementValidations.getErrorMessages().get(0).getDefinition());
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
          }
        }
      }

    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Could not get the data elements validation for mdrId " + mdrId);
    } catch (NumberFormatException e) {
      logger.debug("Value is not a number... " + mdrId);
    }
  }
}
