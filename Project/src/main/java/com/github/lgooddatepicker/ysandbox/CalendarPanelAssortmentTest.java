package com.github.lgooddatepicker.ysandbox;

import com.github.lgooddatepicker.calendarpanel.CalendarPanel;
import com.github.lgooddatepicker.datepicker.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateHighlightPolicy;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * CalendarPanelAssortmentTest.
 */
public class CalendarPanelAssortmentTest {

    public static void main(String[] args) {

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Create a frame, a panel, and our demo buttons.
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////
        // This section creates CalendarPanels, with various features. (presetting preferred.)
        //
        int rowMultiplier = 4;
        CalendarPanel calendarPanel;
        
        // Create a settings variable for repeated use.
        DatePickerSettings dateSettings;
        int row = rowMultiplier;

        // Create a CalendarPanel: With default settings
        calendarPanel = new CalendarPanel();
        panel.add(calendarPanel);

        // Create a CalendarPanel: With highlight policy.
        dateSettings = new DatePickerSettings();
        dateSettings.setHighlightPolicy(new SampleHighlightPolicy());
        calendarPanel = new CalendarPanel(dateSettings);
        panel.add(calendarPanel);

        // Create a CalendarPanel: With veto policy.
        // Note: Veto policies can only be set after constructing the CalendarPanel.
        dateSettings = new DatePickerSettings();
        calendarPanel = new CalendarPanel(dateSettings);
        dateSettings.setVetoPolicy(new SampleDateVetoPolicy());
        panel.add(calendarPanel);

        // Create a CalendarPanel: With both policies.
        // Note: Veto policies can only be set after constructing the CalendarPanel.
        dateSettings = new DatePickerSettings();
        dateSettings.setHighlightPolicy(new SampleHighlightPolicy());
        calendarPanel = new CalendarPanel(dateSettings);
        dateSettings.setVetoPolicy(new SampleDateVetoPolicy());
        panel.add(calendarPanel);

        // Create a CalendarPanel: Change calendar size.
        dateSettings = new DatePickerSettings();
        int newHeight = (int) (dateSettings.getSizeDatePanelMinimumHeight() * 1.7);
        int newWidth = (int) (dateSettings.getSizeDatePanelMinimumWidth() * 1.7);
        dateSettings.setSizeDatePanelMinimumHeight(newHeight);
        dateSettings.setSizeDatePanelMinimumWidth(newWidth);
        calendarPanel = new CalendarPanel(dateSettings);
        panel.add(calendarPanel);

        // Create a CalendarPanel: Custom color.
        dateSettings = new DatePickerSettings();
        dateSettings.setColorBackgroundCalendarPanel(Color.BLUE);
        dateSettings.setColorBackgroundWeekdayLabels(Color.PINK);
        dateSettings.setColorBackgroundMonthAndYear(Color.ORANGE);
        dateSettings.setColorBackgroundTodayAndClear(Color.ORANGE);
        dateSettings.setColorBackgroundNavigateYearMonthButtons(Color.MAGENTA);
        calendarPanel = new CalendarPanel(dateSettings);
        panel.add(calendarPanel);

        // Create a CalendarPanel: Change first weekday.
        dateSettings = new DatePickerSettings();
        dateSettings.setFirstDayOfWeek(DayOfWeek.THURSDAY);
        calendarPanel = new CalendarPanel(dateSettings);
        panel.add(calendarPanel);

        // Create a CalendarPanel: No empty dates. (aka null)
        dateSettings = new DatePickerSettings();
        dateSettings.setAllowEmptyDates(false);
        calendarPanel = new CalendarPanel(dateSettings);
        panel.add(calendarPanel);
        

        // Create a CalendarPanel: Localized (Greek)
        Locale datePickerLocale = new Locale("el");
        dateSettings = new DatePickerSettings(datePickerLocale);
        dateSettings.setInitialDate(LocalDate.of(2016, Month.APRIL, 15));
        calendarPanel = new CalendarPanel(dateSettings);
        panel.add(calendarPanel);
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////
        // This section creates CalendarPanels, with various features. (postsetting preferred.)
        //

        // Create a CalendarPanel: With default settings
        calendarPanel = new CalendarPanel();
        panel.add(calendarPanel);

        // Create a CalendarPanel: With highlight policy.
        dateSettings = new DatePickerSettings();
        calendarPanel = new CalendarPanel(dateSettings);
        dateSettings.setHighlightPolicy(new SampleHighlightPolicy());
        panel.add(calendarPanel);

        // Create a CalendarPanel: With veto policy.
        // Note: Veto policies can only be set after constructing the CalendarPanel.
        dateSettings = new DatePickerSettings();
        calendarPanel = new CalendarPanel(dateSettings);
        dateSettings.setVetoPolicy(new SampleDateVetoPolicy());
        panel.add(calendarPanel);

        // Create a CalendarPanel: With both policies.
        // Note: Veto policies can only be set after constructing the CalendarPanel.
        dateSettings = new DatePickerSettings();
        calendarPanel = new CalendarPanel(dateSettings);
        dateSettings.setHighlightPolicy(new SampleHighlightPolicy());
        dateSettings.setVetoPolicy(new SampleDateVetoPolicy());
        panel.add(calendarPanel);

        // Create a CalendarPanel: Change calendar size.
        dateSettings = new DatePickerSettings();
        calendarPanel = new CalendarPanel(dateSettings);
        int newHeight2 = (int) (dateSettings.getSizeDatePanelMinimumHeight() * 1.7);
        int newWidth2 = (int) (dateSettings.getSizeDatePanelMinimumWidth() * 1.7);
        dateSettings.setSizeDatePanelMinimumHeight(newHeight2);
        dateSettings.setSizeDatePanelMinimumWidth(newWidth2);
        panel.add(calendarPanel);

        // Create a CalendarPanel: Custom color.
        dateSettings = new DatePickerSettings();
        calendarPanel = new CalendarPanel(dateSettings);
        dateSettings.setColorBackgroundCalendarPanel(Color.BLUE);
        dateSettings.setColorBackgroundWeekdayLabels(Color.PINK);
        dateSettings.setColorBackgroundMonthAndYear(Color.ORANGE);
        dateSettings.setColorBackgroundTodayAndClear(Color.ORANGE);
        dateSettings.setColorBackgroundNavigateYearMonthButtons(Color.MAGENTA);
        panel.add(calendarPanel);

        // Create a CalendarPanel: Change first weekday.
        dateSettings = new DatePickerSettings();
        calendarPanel = new CalendarPanel(dateSettings);
        dateSettings.setFirstDayOfWeek(DayOfWeek.THURSDAY);
        panel.add(calendarPanel);

        // Create a CalendarPanel: No empty dates. (aka null)
        dateSettings = new DatePickerSettings();
        dateSettings.setAllowEmptyDates(false);
        calendarPanel = new CalendarPanel(dateSettings);
        panel.add(calendarPanel);
        

        // Create a CalendarPanel: Localized (Greek)
        Locale datePickerLocale2 = new Locale("el");
        dateSettings = new DatePickerSettings(datePickerLocale2);
        dateSettings.setInitialDate(LocalDate.of(2016, Month.APRIL, 15));
        calendarPanel = new CalendarPanel(dateSettings);
        panel.add(calendarPanel);

        // Display the frame.
        frame.pack();
        frame.validate();
        int maxWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
        int maxHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
        frame.setSize(640, 480);
        frame.setLocation(maxWidth / 2, maxHeight / 2);
        frame.setVisible(true);
    }
    
    

    /**
     * SampleDateVetoPolicy, A veto policy is a way to disallow certain dates from being selected in
     * calendar. A vetoed date cannot be selected by using the keyboard or the mouse.
     */
    private static class SampleDateVetoPolicy implements DateVetoPolicy {

        /**
         * isDateAllowed, Return true if a date should be allowed, or false if a date should be
         * vetoed.
         */
        @Override
        public boolean isDateAllowed(LocalDate date) {
            // Disallow days 7 to 11.
            if ((date.getDayOfMonth() >= 7) && (date.getDayOfMonth() <= 11)) {
                return false;
            }
            // Disallow odd numbered saturdays.
            if ((date.getDayOfWeek() == DayOfWeek.SATURDAY) && ((date.getDayOfMonth() % 2) == 1)) {
                return false;
            }
            // Allow all other days.
            return true;
        }
    }

    /**
     * SampleHighlightPolicy, A highlight policy is a way to visually highlight certain dates in the
     * calendar. These may be holidays, or weekends, or other significant dates.
     */
    private static class SampleHighlightPolicy implements DateHighlightPolicy {

        /**
         * getHighlightInformationOrNull, Implement this function to indicate if a date should be
         * highlighted, and what highlighting details should be used for the highlighted date.
         *
         * If a date should be highlighted, then return an instance of HighlightInformation. If the
         * date should not be highlighted, then return null.
         *
         * You may (optionally) fill out the fields in the HighlightInformation class to give any
         * particular highlighted day a unique foreground color, background color, or tooltip text.
         * If the color fields are null, then the default highlighting colors will be used. If the
         * tooltip field is null (or empty), then no tooltip will be displayed.
         *
         * Dates that are passed to this function will never be null.
         */
        @Override
        public HighlightInformation getHighlightInformationOrNull(LocalDate date) {
            // Highlight a chosen date, with a tooltip and a red background color.
            if (date.getDayOfMonth() == 25) {
                return new HighlightInformation(Color.red, null, "It's the 25th!");
            }
            // Highlight all Saturdays with a unique background and foreground color.
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                return new HighlightInformation(Color.orange, Color.yellow, "It's Saturday!");
            }
            // Highlight all Sundays with default colors and a tooltip.
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return new HighlightInformation(null, null, "It's Sunday!");
            }
            // All other days should not be highlighted.
            return null;
        }
    }

}
