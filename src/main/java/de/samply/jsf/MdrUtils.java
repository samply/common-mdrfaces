
package de.samply.jsf;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Designation;
import de.samply.common.mdrclient.domain.Label;
import de.samply.web.mdrfaces.MdrContext;
import de.samply.web.mdrfaces.MdrDataElementBean;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Frequently called methods related to the MDR.
 *
 * @author diogo
 *
 */
public final class MdrUtils {

  /**
   * A regular expression that defines an MDR ID.
   */
  private static final String MDR_ID_REGEX = "^urn:[a-zA-Z0-9-_]*:[a-zA-Z0-9-_]*:[a-zA-Z0-9:.]*$";

  /**
   * A regular expression that defines an MDR ID from a data element.
   */
  private static final String MDR_DATAELEMENT_ID_REGEX =
      "^urn:[a-zA-Z0-9-_]*:dataelement:[a-zA-Z0-9:.]*$";

  /**
   * A regular expression that defines an MDR ID from a record.
   */
  private static final String MDR_RECORD_ID_REGEX = "^urn:[a-zA-Z0-9-_]*:record:[a-zA-Z0-9:.]*$";

  /**
   * Check whether a String is a valid MdrId.
   *
   * @param mdrId
   *            the MDR element ID e.g. "urn:mdr:dataelement:2:1.0"
   * @return true if it is an MdrId, false otherwise
   */
  public static boolean isValidMdrId(final String mdrId) {
    Pattern pattern = Pattern.compile(MDR_ID_REGEX);
    Matcher matcher = pattern.matcher(mdrId);
    return matcher.matches();
  }

  /**
   * Check whether a String is a valid data element MdrId.
   *
   * @param mdrId
   *            the MDR element ID e.g. "urn:mdr:dataelement:2:1.0"
   * @return true if it a value data element MdrId, false otherwise
   */
  public static boolean isValidDataElementMdrId(final String mdrId) {
    Pattern pattern = Pattern.compile(MDR_DATAELEMENT_ID_REGEX);
    Matcher matcher = pattern.matcher(mdrId);
    return matcher.matches();
  }

  /**
   * Check whether a String is a valid record MdrId.
   *
   * @param mdrId
   *            the MDR element ID e.g. "urn:mdr:dataelement:2:1.0"
   * @return true if it a value record MdrId, false otherwise
   */
  public static boolean isValidRecordMdrId(final String mdrId) {
    Pattern pattern = Pattern.compile(MDR_RECORD_ID_REGEX);
    Matcher matcher = pattern.matcher(mdrId);
    return matcher.find();
  }

  /**
   * Get the text of the label of an MDR element in a form.
   *
   * @param mdrId
   *            the MDR element ID e.g. "urn:mdr:dataelement:2:1.0"
   * @return the label of a MDR data element
   * @throws MdrInvalidResponseException
   *             if there is an unexpected response
   * @throws MdrConnectionException
   *             if it was not possible to establish a connection with the MDR
   * @throws ExecutionException
   *             if there is an execution error
   */
  public static String getLabelText(final String mdrId) throws MdrConnectionException,
      MdrInvalidResponseException, ExecutionException {
    Logger logger = LoggerFactory.getLogger(MdrUtils.class.getName());
    logger.info("Checking the label of..." + mdrId);

    String labelText = "";
    if (MdrUtils.isValidDataElementMdrId(mdrId)) { // avoid DataElementBean exception
      MdrDataElementBean dataElementBean = new MdrDataElementBean();
      labelText = dataElementBean.getLabelText(mdrId);
    } else if (MdrUtils.isValidRecordMdrId(mdrId)) {
      MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

      List<Label> recordLabel = mdrClient.getRecordLabel(mdrId, JsfUtils.getLocaleLanguage(),
          JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());
      if (!recordLabel.isEmpty()) {
        labelText = recordLabel.get(0).getDesignation();
      }
    }
    return labelText;
  }

  /**
   * Get the error message, in the current locale, describing that a data attribute is not
   * available.
   *
   * @return the error message, in the current locale, describing that a data attribute is not
   *        available.
   */
  public static String getTextNotAvailableInLocale() {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ResourceBundle resourceBundle = facesContext.getApplication()
        .getResourceBundle(facesContext, "general");
    String notAvailableInLocale = resourceBundle.getString("notAvailableInLocale");
    return "< " + notAvailableInLocale + JsfUtils.getDisplayLanguage() + " >";
  }

  /**
   * Get the available designation of a data element from its designations list.
   *
   * @param designations
   *            the designation list of a MDR data element
   * @return the first available definition of a MDR data element, or an error message if not
   *        available
   */
  public static String getDesignation(final List<Designation> designations) {
    return designations.size() >= 1 ? designations.get(0).getDesignation()
        : getTextNotAvailableInLocale();
  }

  /**
   * Get the available definition of a data element from its designations list.
   *
   * @param designations
   *            the designation list of a MDR data element
   * @return the first available definition of a MDR data element, or an error message if not
   *        available
   */
  public static String getDefinition(final List<Designation> designations) {
    return designations.size() >= 1 ? designations.get(0).getDefinition()
        : getTextNotAvailableInLocale();
  }

  /**
   * Prevent instantiation - utility class.
   */
  private MdrUtils() {
  }
}
