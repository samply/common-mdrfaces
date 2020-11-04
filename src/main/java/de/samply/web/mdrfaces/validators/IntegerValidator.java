package de.samply.web.mdrfaces.validators;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
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
 * Validator for integers.
 *
 * @author diogo
 */
@FacesValidator("IntegerValidator")
public class IntegerValidator implements Validator {

  /**
   * A integer regular expression.
   */
  private static final String DIGIT_ONLY_REGEX = "^[0-9]*$";

  /**
   * Logging instance for this class.
   */
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * MDR client instance.
   */
  private MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

  /**
   * The pattern of a integer regular expression.
   */
  private Pattern pattern;

  /**
   * Create a new integer validator.
   */
  public IntegerValidator() {
    pattern = Pattern.compile(DIGIT_ONLY_REGEX);
  }

  @Override
  public final void validate(final FacesContext context, final UIComponent component,
      final Object value)
      throws ValidatorException {
    String mdrId = (String) component.getAttributes().get("mdrId");

    Matcher matcher = pattern.matcher(value.toString());
    if (!matcher.matches()) {

      Validations dataElementValidations;
      try {
        dataElementValidations = mdrClient
            .getDataElementValidations(mdrId, JsfUtils.getLocaleLanguage(),
                JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

        FacesMessage msg = new FacesMessage(
            dataElementValidations.getErrorMessages().get(0).getDesignation(),
            dataElementValidations.getErrorMessages().get(0).getDefinition());
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        throw new ValidatorException(msg);
      } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
        logger.debug("Could not get the data elements validation for mdrId " + mdrId);
      }

    }
  }
}
