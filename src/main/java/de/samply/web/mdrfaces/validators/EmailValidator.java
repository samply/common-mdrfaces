package de.samply.web.mdrfaces.validators;

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
 * Validator for email addresses.
 *
 * @author diogo
 */
@FacesValidator("EmailValidator")
public class EmailValidator implements Validator {

  /**
   * Regular expression that defines an email.
   */
  static final String EMAIL_REGEX = "^\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b$";

  /**
   * Pattern from the email regular expression.
   */
  private static Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

  /**
   * Logging instance for this class.
   */
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public final void validate(final FacesContext context, final UIComponent component,
      final Object value)
      throws ValidatorException {
    logger.debug("Validating submitted email -- " + value.toString());
    Matcher matcher = pattern.matcher(value.toString());

    if (!matcher.matches()) {
      FacesMessage msg = new FacesMessage(" E-mail validation failed.",
          "Please provide E-mail address in this format: abcd@abc.com");
      msg.setSeverity(FacesMessage.SEVERITY_ERROR);

      throw new ValidatorException(msg);
    }
  }
}
