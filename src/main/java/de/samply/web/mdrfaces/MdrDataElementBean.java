
package de.samply.web.mdrfaces;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Catalogue;
import de.samply.common.mdrclient.domain.Code;
import de.samply.common.mdrclient.domain.Definition;
import de.samply.common.mdrclient.domain.Designation;
import de.samply.common.mdrclient.domain.EnumDataType;
import de.samply.common.mdrclient.domain.Identification;
import de.samply.common.mdrclient.domain.Meaning;
import de.samply.common.mdrclient.domain.PermissibleValue;
import de.samply.common.mdrclient.domain.RecordDefinition;
import de.samply.common.mdrclient.domain.Slot;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.jsf.JsfUtils;
import de.samply.web.enums.EnumInputType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.faces.bean.ManagedBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MDR data element bean that helps defining widgets from MDR data.
 *
 * @author diogo
 */
@ManagedBean(name = "MdrDataElementBean")
public class MdrDataElementBean {

  /**
   * Name of the JSF MDR ID attribute.
   */
  private static final String MDR_ID_JSF_ATTRIBUTE = "mdrId";

  /**
   * Name of the INPUT TYPE Slot.
   */
  private static final String SLOT_INPUT_TYPE = "inputType";

  /**
   * Logging instance for this class.
   */
  private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  /**
   * MDR client instance.
   */
  private MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

  /**
   * Automatically generated ID of the text input.
   */
  private String genInputTextId;
  /**
   * Automatically generated ID of the date input.
   */
  private String genInputDateId;
  /**
   * Automatically generated ID of the date and time input.
   */
  private String genInputDateTimeId;
  /**
   * Automatically generated ID of the select one (drop-down box) input.
   */
  private String genInputSelectOneId;
  /**
   * Automatically generated ID of the boolean input.
   */
  private String genInputBooleanId;
  /**
   * Automatically generated ID of the time input.
   */
  private String genInputTimeId;
  /**
   * Automatically generated ID of the catalogue input.
   */
  private String genInputCatalogueId;

  /**
   * Todo.
   */
  public MdrDataElementBean() {
    int index = 0;

    genInputTextId = "input_" + index++;
    genInputDateId = "input_" + index++;
    genInputDateTimeId = "input_" + index++;
    genInputSelectOneId = "input_" + index++;
    genInputBooleanId = "input_" + index++;
    genInputTimeId = "input_" + index++;
    genInputCatalogueId = "input_" + index++;
  }

  /**
   * Check if the mdr can find the mdr id.
   *
   * @param mdrId the urn of the dataelement to check for
   * @return true if it is found, false on any error
   */
  public final boolean isElementKnown(final String mdrId) {
    try {
      mdrClient.getDataElementDefinition(mdrId, JsfUtils.getLocaleLanguage(),
          JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());
      return true;
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Could not get data element definition from mdr: " + e.getMessage());
      return false;
    }
  }

  /**
   * Get the text of the label of an MDR element in a form.
   *
   * @param mdrId the MDR element ID e.g. "urn:mdr:dataelement:2:1.0"
   * @return the text label of the requested data element
   */
  public final String getLabelText(final String mdrId) {
    String labelText = "";

    //Use MdrClient Methods for records if record is detected
    if (mdrId.toLowerCase().contains("record")) {

      try {
        RecordDefinition recordDefinition =
            mdrClient.getRecordDefinition(mdrId, JsfUtils.getLocaleLanguage(),
                JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

        if (recordDefinition != null && recordDefinition.size() > 0) {
          labelText = recordDefinition.get(0).getDesignation().trim();
        }

        return labelText;
      } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
        return "?" + mdrId + "?";
      }
    } else {
      try {
        Definition dataElementDefinition = mdrClient
            .getDataElementDefinition(mdrId, JsfUtils.getLocaleLanguage(),
                JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

        if (dataElementDefinition != null && dataElementDefinition.getDesignations().size() > 0) {
          labelText = dataElementDefinition.getDesignations().get(0).getDesignation().trim();
        }

        return labelText;
      } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
        return "?" + mdrId + "?";
      }

    }

  }

  /**
   * Should the text input be rendered for the given data element?.
   *
   * @param mdrId the MDR data element loaded.
   * @return true if the text input should be rendered, false otherwise
   */
  public final boolean isInputTextRendered(final String mdrId) {
    Validations dataElementValidations;
    try {
      dataElementValidations = mdrClient
          .getDataElementValidations(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (hasPermissibleValues(dataElementValidations)) {
        return false;
      }

      String datatype = dataElementValidations.getDatatype();
      return datatype.compareTo(EnumDataType.INTEGER.name()) == 0
          || datatype.compareTo(EnumDataType.FLOAT.name()) == 0
          || datatype.compareTo(EnumDataType.STRING.name()) == 0;
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Input text will not be rendered: " + e.getMessage());
    }
    return false;
  }

  /**
   * Should the date input be rendered for the given data element?.
   *
   * @param mdrId the MDR data element loaded.
   * @return true if the date input should be rendered, false otherwise
   */
  public final boolean isInputDateRendered(final String mdrId) {
    Validations dataElementValidations;
    try {
      dataElementValidations = mdrClient
          .getDataElementValidations(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (hasPermissibleValues(dataElementValidations)) {
        return false;
      }

      if (dataElementValidations.getDatatype().equals(EnumDataType.DATE.name())) {
        return true;
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Input date will not be rendered: " + e.getMessage());
    }
    return false;
  }

  /**
   * Should the date and time input be rendered for the given data element?.
   *
   * @param mdrId the MDR data element loaded.
   * @return true if the date and time input should be rendered, false otherwise
   */
  public final boolean isInputDateTimeRendered(final String mdrId) {
    Validations dataElementValidations;
    try {
      dataElementValidations = mdrClient
          .getDataElementValidations(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (hasPermissibleValues(dataElementValidations)) {
        return false;
      }

      if (dataElementValidations.getDatatype().equals(EnumDataType.DATETIME.name())) {
        return true;
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Input date will not be rendered: " + e.getMessage());
    }
    return false;
  }

  /**
   * Should the drop down box input be rendered for the given data element?.
   *
   * @param mdrId the MDR data element loaded.
   * @return true if the drop down box input should be rendered, false otherwise
   */
  public final boolean isInputSelectOneRendered(final String mdrId) {
    Validations dataElementValidations;
    try {
      dataElementValidations = mdrClient
          .getDataElementValidations(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (hasPermissibleValues(dataElementValidations)) {
        return true;
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Input select one will not be rendered: " + e.getMessage());
    }
    return false;
  }

  /**
   * Should the boolean input be rendered for the given data element?.
   *
   * @param mdrId the MDR data element loaded.
   * @return true if there is a boolean to be rendered, false otherwise
   */
  public final boolean isInputBooleanRendered(final String mdrId) {
    Validations dataElementValidations;
    try {
      dataElementValidations = mdrClient
          .getDataElementValidations(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (hasPermissibleValues(dataElementValidations)) {
        return false;
      }

      if (dataElementValidations.getDatatype().equals(EnumDataType.BOOLEAN.name())) {
        return true;
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Input boolean will not be rendered: " + e.getMessage());
    }
    return false;
  }

  /**
   * Should the time input be rendered for the given data element?.
   *
   * @param mdrId the MDR data element loaded.
   * @return true if a time widget is to be rendered, false otherwise
   */
  public final boolean isInputTimeRendered(final String mdrId) {
    Validations dataElementValidations;
    try {
      dataElementValidations = mdrClient
          .getDataElementValidations(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (hasPermissibleValues(dataElementValidations)) {
        return false;
      }

      if (dataElementValidations.getDatatype().equals(EnumDataType.TIME.name())) {
        return true;
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Input time will not be rendered: " + e.getMessage());
    }
    return false;
  }

  /**
   * Should the catalogue input be rendered for the given data element?.
   *
   * @param mdrId the MDR data element loaded.
   * @return true if the catalogue widget is to be rendered, false otherwise
   */
  public final boolean isInputCatalogueRendered(final String mdrId) {
    Validations dataElementValidations;
    try {
      dataElementValidations = mdrClient
          .getDataElementValidations(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementValidations.getDatatype().equals(EnumDataType.CATALOG.name())) {
        return true;
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Input time will not be rendered: " + e.getMessage());
    }
    return false;
  }

  /**
   * Get the label tool tip text.
   *
   * @return the label tool tip text.
   */
  public final String getlabelTooltipText() {
    String mdrId = JsfUtils.getJsfAttribute(MDR_ID_JSF_ATTRIBUTE);
    try {
      Definition dataElementDefinition = mdrClient
          .getDataElementDefinition(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementDefinition != null && dataElementDefinition.getDesignations() != null
          && !dataElementDefinition.getDesignations().isEmpty()) {
        return dataElementDefinition.getDesignations().get(0).getDefinition();
      }
      return null;
    } catch (ExecutionException | MdrInvalidResponseException | MdrConnectionException e) {
      return null;
    }
  }

  /**
   * Get the input tool tip text.
   *
   * @return the input tool tip text.
   */
  public final String getInputTooltipText() {
    String mdrId = JsfUtils.getJsfAttribute(MDR_ID_JSF_ATTRIBUTE);

    try {
      Definition dataElementDefinition = mdrClient
          .getDataElementDefinition(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementDefinition != null && dataElementDefinition.getDesignations() != null
          && dataElementDefinition.getDesignations().size() > 0) {
        return dataElementDefinition.getDesignations().get(0).getDefinition();
      }
      return null;
    } catch (ExecutionException | MdrInvalidResponseException | MdrConnectionException e) {
      return null;
    }
  }

  /**
   * Should the tooltip be rendered?.
   *
   * @return true if the tooltip should be rendered, false otherwise
   */
  public final boolean isInputTooltipRendered() {
    String text = null;
    String mdrId = JsfUtils.getJsfAttribute(MDR_ID_JSF_ATTRIBUTE);
    try {
      Definition dataElementDefinition = mdrClient.getDataElementDefinition(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementDefinition != null && dataElementDefinition.getDesignations() != null
          && dataElementDefinition.getDesignations().size() > 0) {
        text = dataElementDefinition.getDesignations().get(0).getDefinition();
      }

      if (text != null && text.length() > 0) {
        return true;
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Input tooltip will not be rendered: " + e.getMessage());
    }
    return false;
  }

  /**
   * Get the select items from an enumerated MDR element (the kind that is shown on a drop-down
   * box).
   *
   * @param mdrId of the MDR element
   * @return the select items of a MDR element
   */
  public final ArrayList<PermissibleValue> getSelectItems(final String mdrId) {
    return getSelectItems(mdrId, true);
  }

  /**
   * Get the select items from an enumerated MDR element (the kind that is shown on a drop-down
   * box).
   *
   * @param mdrId of the MDR element
   * @return the select items of a MDR element
   */
  public final ArrayList<PermissibleValue> getSelectItems(final String mdrId,
      final boolean includeEmptyElement) {
    ArrayList<PermissibleValue> permissibleValues = new ArrayList<>();

    Validations dataElementValidations;
    try {
      dataElementValidations = mdrClient
          .getDataElementValidations(mdrId, JsfUtils.getLocaleLanguage(),
              JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());
      if (hasPermissibleValues(dataElementValidations)) {
        logger.debug("There is a list of permissible values for mdrId: " + mdrId);

        // enable an empty value (default selection)
        if (includeEmptyElement) {
          permissibleValues.add(getEmptyPermissableValue());
        }

        permissibleValues.addAll(dataElementValidations.getPermissibleValues());
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug(e.getMessage());
    }
    return permissibleValues;
  }

  /**
   * Todo.
   * @param mdrId Todo.
   * @return Todo.
   * @throws MdrConnectionException Todo.
   * @throws MdrInvalidResponseException Todo.
   * @throws ExecutionException Todo.
   */
  public final String getInputType(final String mdrId)
      throws MdrConnectionException, MdrInvalidResponseException,
      ExecutionException {
    EnumInputType inputType = EnumInputType.SELECT_ONE_MENU;
    String retVal = inputType.toString();

    ArrayList<Slot> slots = mdrClient.getDataElementSlots(mdrId);

    for (Slot slot : slots) {
      if (slot.getSlotName().equalsIgnoreCase(SLOT_INPUT_TYPE)) {
        try {
          retVal = EnumInputType.valueOf(slot.getSlotValue()).toString();
          break;
        } catch (IllegalArgumentException e) {
          logger.warn("Illegal Argument in inputType slot: " + slot.getSlotValue().toString());
          retVal = inputType.toString();
          break;
        }
      }
    }

    return retVal;
  }

  /**
   * Get the select items from a catalogue.
   *
   * @param mdrId the MDR ID of the catalogue
   * @return the select item from a MDR catalogue
   */
  public final ArrayList<Code> getCatalogueSelectItems(final String mdrId) {
    ArrayList<Code> codes = new ArrayList<>();

    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          JsfUtils.getLocaleLanguage(), JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());

      if (dataElementValidations.getDatatype().equals(EnumDataType.CATALOG.name())) {
        Catalogue catalogue = mdrClient.getDataElementCatalogue(mdrId, JsfUtils.getLocaleLanguage(),
            JsfUtils.getAccessToken(), JsfUtils.getUserAuthId());
        codes = new ArrayList<>();

        // enable an empty value (default selection)
        codes.add(getEmptyCode());

        codes.addAll(catalogue.getCodes());
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug(e.getMessage());
    }
    return codes;
  }

  /**
   * Get the ID of the input that will be rendered for the given MDR ID.
   *
   * @param mdrId        the MDR ID of the rendered data element
   * @param givenInputId the input ID optionally given as a composite component attribute
   * @return the ID of the input that will be rendered for the given MDR ID
   */
  public final String getRenderedInputId(final String mdrId, final String givenInputId) {
    String renderedInputId = "";
    if (!givenInputId.isEmpty()) {
      renderedInputId = givenInputId;
    } else {
      if (isInputTextRendered(mdrId)) {
        renderedInputId = genInputTextId;
      } else if (isInputDateRendered(mdrId)) {
        renderedInputId = genInputDateId;
      } else if (isInputDateTimeRendered(mdrId)) {
        renderedInputId = genInputDateTimeId;
      } else if (isInputSelectOneRendered(mdrId)) {
        renderedInputId = genInputSelectOneId;
      } else if (isInputBooleanRendered(mdrId)) {
        renderedInputId = genInputBooleanId;
      } else if (isInputTimeRendered(mdrId)) {
        renderedInputId = genInputTimeId;
      }
    }

    return renderedInputId;
  }

  /**
   * Get an empty permissible value to serve as an empty option.
   *
   * @return an empty permissible value to serve as an empty option
   */
  private PermissibleValue getEmptyPermissableValue() {
    PermissibleValue emptySelection = new PermissibleValue();
    ArrayList<Meaning> meanings = new ArrayList<Meaning>();
    Meaning meaning = new Meaning();
    meaning.setDesignation("");
    meanings.add(meaning);
    emptySelection.setMeanings(meanings);
    emptySelection.setValue("");
    return emptySelection;
  }

  /**
   * Get an empty catalogue code value to serve as an empty option.
   *
   * @return an empty catalogue code to serve as an empty option
   */
  private Code getEmptyCode() {
    Code code = new Code();
    code.setCode("");
    List<Designation> designations = new ArrayList<Designation>();
    Designation designation = new Designation();
    designation.setDefinition("");
    designation.setDesignation("");
    designations.add(designation);
    code.setDesignations(designations);
    Identification identification = new Identification();
    code.setIdentification(identification);
    code.setIsValid(true);
    List<Object> subCodes = new ArrayList<>();
    code.setSubCodes(subCodes);
    return code;
  }

  /**
   * Check if validations include permissible values.
   *
   * @param dataElementValidations the data element validations
   * @return true if {@link Validations} include permissible values, false otherwise
   */
  private boolean hasPermissibleValues(final Validations dataElementValidations) {
    return dataElementValidations == null || dataElementValidations.getPermissibleValues() != null
        && dataElementValidations.getPermissibleValues().size() > 0;
  }

  /**
   * Get the boolean input ID.
   *
   * @param mdrId        the MDR ID of the rendered data element
   * @param givenInputId the input ID optionally given as a composite component attribute
   * @return the ID of the boolean input
   */
  public final String getInputBooleanId(final String mdrId, final String givenInputId) {
    return givenInputId.isEmpty() || !isInputBooleanRendered(mdrId) ? genInputBooleanId
        : givenInputId;
  }

  /**
   * Get the date input ID.
   *
   * @param mdrId        the MDR ID of the rendered data element
   * @param givenInputId the input ID optionally given as a composite component attribute
   * @return the ID of the date input
   */
  public final String getInputDateId(final String mdrId, final String givenInputId) {
    return givenInputId.isEmpty() || !isInputDateRendered(mdrId) ? genInputDateId : givenInputId;
  }

  /**
   * Get the date and time input ID.
   *
   * @param mdrId        the MDR ID of the rendered data element
   * @param givenInputId the input ID optionally given as a composite component attribute
   * @return the ID of the date and time input
   */
  public final String getInputDateTimeId(final String mdrId, final String givenInputId) {
    return givenInputId.isEmpty() || !isInputDateTimeRendered(mdrId) ? genInputDateTimeId
        : givenInputId;
  }

  /**
   * Get the select one input ID.
   *
   * @param mdrId        the MDR ID of the rendered data element
   * @param givenInputId the input ID optionally given as a composite component attribute
   * @return the ID of the select one input
   */
  public final String getInputSelectOneId(final String mdrId, final String givenInputId) {
    return givenInputId.isEmpty() || !isInputSelectOneRendered(mdrId) ? genInputSelectOneId
        : givenInputId;
  }

  /**
   * Get the text input ID.
   *
   * @param mdrId        the MDR ID of the rendered data element
   * @param givenInputId the input ID optionally given as a composite component attribute
   * @return the ID of the text input
   */
  public final String getInputTextId(final String mdrId, final String givenInputId) {
    return givenInputId.isEmpty() || !isInputTextRendered(mdrId) ? genInputTextId : givenInputId;
  }

  /**
   * Get the time input ID.
   *
   * @param mdrId        the MDR ID of the rendered data element
   * @param givenInputId the input ID optionally given as a composite component attribute
   * @return the ID of the time input
   */
  public final String getInputTimeId(final String mdrId, final String givenInputId) {
    return givenInputId.isEmpty() || !isInputTimeRendered(mdrId) ? genInputTimeId : givenInputId;
  }

  /**
   * Get the catalogue input ID.
   *
   * @param mdrId        the MDR ID of the rendered data element
   * @param givenInputId the input ID optionally given as a composite component attribute
   * @return the ID of the catalogue input
   */
  public final String getInputCatalogueId(final String mdrId, final String givenInputId) {
    return givenInputId.isEmpty() || !isInputCatalogueRendered(mdrId) ? genInputCatalogueId
        : givenInputId;
  }
}
