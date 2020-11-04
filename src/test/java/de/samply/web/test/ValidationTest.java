
package de.samply.web.test;

import static org.junit.Assert.assertEquals;

import de.samply.web.enums.EnumDateFormat;
import de.samply.web.enums.EnumTimeFormat;
import de.samply.web.mdrfaces.validators.DateTimeValidator;
import de.samply.web.mdrfaces.validators.DateValidator;
import de.samply.web.mdrfaces.validators.TimeValidator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.junit.Test;

/**
 * Test class for data type validations. 
 * 
 * @author diogo
 *
 */
public class ValidationTest {

    /**
     * Test time formats from the {@link TimeValidator}.
     */
    @Test
    public final void testTimeFormats() {
        DateFormat formatter = new SimpleDateFormat(TimeValidator.getTimePattern(EnumTimeFormat.HOURS_24));
        String pattern = ((SimpleDateFormat) formatter).toPattern();
        assertEquals("HH:mm", pattern);

        formatter = new SimpleDateFormat(TimeValidator.getTimePattern(EnumTimeFormat.HOURS_24_WITH_SECONDS));
        pattern = ((SimpleDateFormat) formatter).toPattern();
        assertEquals("HH:mm:ss", pattern);

        formatter = new SimpleDateFormat(TimeValidator.getTimePattern(EnumTimeFormat.HOURS_12));
        pattern = ((SimpleDateFormat) formatter).toPattern();
        assertEquals("h:mm a", pattern);

        formatter = new SimpleDateFormat(TimeValidator.getTimePattern(EnumTimeFormat.HOURS_12_WITH_SECONDS));
        pattern = ((SimpleDateFormat) formatter).toPattern();
        assertEquals("h:mm:ss a", pattern);

        formatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY);
        pattern = ((SimpleDateFormat) formatter).toPattern();
        assertEquals("HH:mm", pattern);

        formatter = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.GERMANY);
        pattern = ((SimpleDateFormat) formatter).toPattern();
        assertEquals("HH:mm:ss", pattern);
    }

    /**
     * Test time formats from the {@link DateValidator}.
     */
    @Test
    public final void testDateFormats() {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMANY);
        String pattern = ((SimpleDateFormat) formatter).toPattern();
        assertEquals("dd.MM.yyyy", pattern);

        String localPattern = ((SimpleDateFormat) formatter).toPattern();
        assertEquals("dd.MM.yyyy", localPattern);

        String datepickerPattern = DateValidator.getDatePattern(EnumDateFormat.ISO_8601_WITH_DAYS);
        assertEquals("yyyy-MM-dd", datepickerPattern);

        formatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.GERMANY);
        assertEquals("dd.MM.yyyy", ((SimpleDateFormat) formatter).toPattern());
    }

    /**
     * Test time formats from the {@link DateValidator}.
     */
    @Test
    public final void testDateTimeFormats() {
        String pattern = DateTimeValidator.getDateTimePattern(EnumDateFormat.DIN_5008, EnumTimeFormat.HOURS_12);
        assertEquals("MM.yyyy h:mm a", pattern);

        pattern = DateTimeValidator.getDateTimePattern(EnumDateFormat.DIN_5008_WITH_DAYS,
                EnumTimeFormat.HOURS_12_WITH_SECONDS);
        assertEquals("dd.MM.yyyy h:mm:ss a", pattern);

        pattern = DateTimeValidator.getDateTimePattern(EnumDateFormat.ISO_8601_WITH_DAYS, EnumTimeFormat.HOURS_24);
        assertEquals("yyyy-MM-dd HH:mm", pattern);

        pattern = DateTimeValidator.getDateTimePattern(EnumDateFormat.LOCAL_DATE_WITH_DAYS, EnumTimeFormat.LOCAL_TIME);
        assertEquals("dd.MM.yyyy HH:mm", pattern);
    }

}
