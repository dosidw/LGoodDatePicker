package com.github.lgooddatepicker.calendarpanel;

import java.time.*;
import com.privatejgoodies.forms.layout.FormLayout;
import com.privatejgoodies.forms.factories.CC;
import com.github.lgooddatepicker.datepicker.DatePicker;
import com.github.lgooddatepicker.datepicker.DatePickerSettings;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.DateFormatSymbols;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;
import com.github.lgooddatepicker.optionalusertools.DateHighlightPolicy;
import com.github.lgooddatepicker.zinternaltools.CalendarSelectionEvent;
import com.github.lgooddatepicker.zinternaltools.InternalUtilities;
import com.github.lgooddatepicker.zinternaltools.JIntegerTextField;
import com.github.lgooddatepicker.zinternaltools.JIntegerTextField.IntegerTextFieldNumberChangeListener;
import com.github.lgooddatepicker.optionalusertools.CalendarSelectionListener;
import com.github.lgooddatepicker.zinternaltools.MouseLiberalAdapter;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;
import com.privatejgoodies.forms.layout.CellConstraints;

/**
 * CalendarPanel,
 *
 * This implements a swing component that displays and draws a calendar. The CalendarPanel has
 * controls for changing the current month or year, and for selecting dates.
 *
 * In most cases, you will not need to create your own instances of CalendarPanel. The DatePicker
 * class automatically creates its own instances of CalendarPanel whenever the user clicks on the
 * "toggle calendar" button. However, the CalendarPanel can also (optionally) be used as an
 * independent component when desired.
 *
 * Life cycle of CalendarPanel inside a DatePicker: Each time that the user clicks the toggle
 * calendar button on a date picker, a new CalendarPanel instance is created and displayed, within
 * of a new instance of CustomPopup. The calendar panel instance is closed and disposed each time
 * that the date picker popup is closed.
 */
public class CalendarPanel
        extends JPanel implements IntegerTextFieldNumberChangeListener {

    /**
     * dateLabels, This holds a list of all the date labels in the calendar, including ones that
     * currently have dates or ones that are blank. This should always have exactly 42 labels. Date
     * labels are reused when the currently displayed month or year is changed.
     */
    private ArrayList<JLabel> dateLabels;

    /**
     * calendarSelectionListeners, This holds a list of calendar selection listeners that wish to be
     * notified each time that a date is selected in the calendar panel.
     */
    private ArrayList<CalendarSelectionListener> calendarSelectionListeners
            = new ArrayList<CalendarSelectionListener>();

    /**
     * constantFirstWeekdayLabelCell, This constant indicates the location of the first weekday
     * label inside of the center panel.
     */
    static private final Point constantFirstWeekdayLabelCell = new Point(2, 1);

    /**
     * constantFirstDateLabelCell, This constant indicates the location of the first date label
     * inside of the center panel.
     */
    static private final Point constantFirstDateLabelCell = new Point(2, 5);

    /**
     * constantSizeOfCenterPanelBorders, This constant indicates the (total) size of all the borders
     * in the center panel, in pixels.
     */
    static private final Dimension constantSizeOfCenterPanelBorders = new Dimension(2, 5);

    /**
     * displayedSelectedDate, This stores a date that will be highlighted in the calendar as the
     * "selected date", or it holds null if no date has been selected. This date is copied from the
     * date picker when the calendar is opened. This should not be confused with the "lastValidDate"
     * of a date picker object. This variable holds the selected date only for display purposes and
     * internal CalendarPanel use.
     */
    private LocalDate displayedSelectedDate = null;

    /**
     * displayedYearMonth, This stores the currently displayed year and month. This defaults to the
     * current year and month. This will never be null.
     */
    private YearMonth displayedYearMonth = YearMonth.now();

    /**
     * isIndependentCalendarPanel, This indicates whether or not this is an independent calendar
     * panel. This is true if this is an independent calendar panel, or false if this is a private
     * calendar panel for a DatePicker instance.
     */
    private boolean isIndependentCalendarPanel;

    /**
     * labelIndicatorEmptyBorder, This stores the empty border and the empty border size for the
     * following labels: Month indicator, Year indicator, Set date to Today, Clear date.
     */
    private EmptyBorder labelIndicatorEmptyBorder = new EmptyBorder(3, 2, 3, 2);

    /**
     * settings, This holds a reference to the date picker settings for this calendar panel. This
     * will never be null.
     *
     * Programmer note: This should never be set to null, because setting this to null can cause
     * NullPointerExceptions. Apparently, in certain operating systems, the pop-up for the calendar
     * panel can be closed before the date is set in the parent date picker.
     */
    private DatePickerSettings settings;

    /**
     * weekdayLabels, This holds a list of all the weekday labels in the calendar. This should
     * always have exactly 7 labels. Weekday labels are reused when the currently displayed month or
     * year is changed.
     */
    private ArrayList<JLabel> weekdayLabels;

    /**
     * weekdayLabelExtras, This holds the extra labels in the weekday area, which do not hold days
     * of the week, but must match the color scheme of the weekday labels.
     */
    private ArrayList<JLabel> weekdayLabelExtras;

    /**
     * yearTextField, The year text field is displayed any time that the user clicks the ellipsis
     * (...) inside the year selection drop down menu. This field allows the user to type year
     * numbers using the keyboard when desired.
     */
    private JIntegerTextField yearTextField;

    /**
     * JFormDesigner GUI components, These variables are automatically generated by JFormDesigner.
     * This section should not be modified by hand, but only modified from within the JFormDesigner
     * program.
     */
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel headerControlsPanel;
	private JButton buttonPreviousYear;
	private JButton buttonPreviousMonth;
	private JPanel monthAndYearOuterPanel;
	private JPanel monthAndYearInnerPanel;
	private JLabel labelMonth;
	private JLabel labelYear;
	private JButton buttonNextMonth;
	private JButton buttonNextYear;
	private JPanel centerPanel;
	private JPanel footerPanel;
	private JLabel labelSetDateToToday;
	private JLabel labelClearDate;
	private JPanel yearEditorPanel;
	private JButton doneEditingYearButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    /**
     * Constructor, Independent CalendarPanel with default settings. This creates an independent
     * calendar panel with a default set of DatePickerSettings. The calendar panel will use the
     * default operating system locale and language.
     */
    public CalendarPanel() {
        this(null, true);
    }

    /**
     * Constructor, Independent CalendarPanel with supplied settings. This creates This creates an
     * independent calendar panel with the supplied date picker settings. If the datePickerSettings
     * are null, then a default settings instance will be created and applied to the CalendarPanel.
     */
    public CalendarPanel(DatePickerSettings settings) {
        this(settings, true);
    }

    /**
     * Constructor, Private CalendarPanel For DatePicker. Important Note: This function is only
     * intended to be called from the DatePicker class. This creates a calendar panel from an
     * existing DatePicker, for internal usage by that DatePicker instance. This will use the
     * settings from the DatePicker instance.
     *
     * Technical note: This constructor is only called from the DatePicker.openPopup() function. A
     * new CalendarPanel is created every time the popup is opened. Therefore, any
     * DatePickerSettings variables that are initialized in this constructor are automatically able
     * to correctly handle being set either before or after, a DatePicker is constructed.
     */
    public CalendarPanel(DatePicker parentDatePicker) {
        this(parentDatePicker.getSettings(), false);
    }

    /**
     * Constructor, Private Full Constructor. This will create the calendar panel instance. This
     * constructor is private and can only be called from other CalendarPanel constructors.
     */
    private CalendarPanel(DatePickerSettings datePickerSettings,
            boolean isIndependentCalendarPanelInstance) {
        // If no settings were supplied, then create a default settings instance.
        if (datePickerSettings == null) {
            datePickerSettings = new DatePickerSettings();
        }
        // Save the constructor parameters.
        this.settings = datePickerSettings;
        this.isIndependentCalendarPanel = isIndependentCalendarPanelInstance;
        // If this is an independent calendar panel, store the parent calendar panel in the 
        // settings instance.
        if (isIndependentCalendarPanel) {
            settings.zSetParentCalendarPanel(this);
        }
        // Initialize the components.
        initComponents();

        // Add needed mouse listeners to the today and clear buttons. 
        zAddMouseListenersToTodayAndClearButtons();

        // Create the yearTextField, and add it to the yearEditorPanel.
        yearTextField = new JIntegerTextField(4);
        yearTextField.setMaximumValue(Year.MAX_VALUE);
        yearTextField.setMinimumValue(Year.MIN_VALUE);
        yearTextField.setMargin(new Insets(1, 1, 1, 1));
        yearEditorPanel.add(yearTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        // Add the text change listener to the yearTextField.
        yearTextField.numberChangeListener = this;

        // Initialize the doneEditingYearButton.
        doneEditingYearButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        doneEditingYearButton.setText("\u2713");

        // Set the calendar panel to be opaque.
        setOpaque(true);

        // Generate and add the date labels and weekday labels.
        // These are only generated once.
        addDateLabels();
        addWeekdayLabels();

        // Shrink the buttons for previous and next year and month.
        buttonPreviousYear.setMargin(new java.awt.Insets(1, 2, 1, 2));
        buttonNextYear.setMargin(new java.awt.Insets(1, 2, 1, 2));
        buttonPreviousMonth.setMargin(new java.awt.Insets(1, 2, 1, 2));
        buttonNextMonth.setMargin(new java.awt.Insets(1, 2, 1, 2));

        // Set the label indicators to their default states.
        zLabelIndicatorsAllSetColorsToDefaultState();

        // If this is an an independent calendar panel, apply the needed settings at 
        // independent CalendarPanel construction.
        if (isIndependentCalendarPanel) {
            settings.yApplyNeededSettingsAtIndependentCalendarPanelConstruction();
        }

        // If the selected date is null, set the calendar to show the current month and year.
        // If the selected date is not null (probably because it was changed by independent 
        // calendar panel settings), then set the calendar to show the year and month of the 
        // selected date.
        if (displayedSelectedDate == null) {
            drawCalendar(YearMonth.now());
        } else {
            YearMonth selectedDateYearMonth = YearMonth.of(
                    displayedSelectedDate.getYear(), displayedSelectedDate.getMonth());
            drawCalendar(selectedDateYearMonth);
        }
    }

    /**
     * addDateLabels, This adds a set of 42 date labels to the calendar, and ties each of those
     * labels to a mouse click event handler. The date labels are reused any time that the calendar
     * is redrawn.
     */
    private void addDateLabels() {
        dateLabels = new ArrayList<JLabel>();
        for (int i = 0; i < 42; ++i) {
            int dateLabelColumnX = ((i % 7)) + constantFirstDateLabelCell.x;
            int dateLabelRowY = ((i / 7) + constantFirstDateLabelCell.y);
            JLabel dateLabel = new JLabel();
            dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dateLabel.setVerticalAlignment(SwingConstants.CENTER);
            dateLabel.setBackground(Color.white);
            dateLabel.setBorder(null);
            dateLabel.setOpaque(true);
            dateLabel.setText("" + i);
            CellConstraints constraints = CC.xy(dateLabelColumnX, dateLabelRowY);
            centerPanel.add(dateLabel, constraints);
            dateLabels.add(dateLabel);
            // Add a mouse click listener for every date label, even the blank ones.
            dateLabel.addMouseListener(new MouseLiberalAdapter() {
                @Override
                public void mouseLiberalClick(MouseEvent e) {
                    dateLabelMousePressed(e);
                }
            });
        }
    }

    /**
     * addWeekdayLabels, This adds a set of 7 weekday labels to the calendar panel. The text of
     * these labels is set with locale sensitive weekday names each time that the calendar is
     * redrawn.
     */
    private void addWeekdayLabels() {
        weekdayLabels = new ArrayList<JLabel>();
        int weekdayLabelRowY = constantFirstWeekdayLabelCell.y;
        int weekdayLabelHeightInCells = 3;
        for (int i = 0; i < 7; ++i) {
            int weekdayLabelColumnX = (i + constantFirstWeekdayLabelCell.x);
            JLabel weekdayLabel = new JLabel();
            weekdayLabel.setHorizontalAlignment(SwingConstants.CENTER);
            weekdayLabel.setVerticalAlignment(SwingConstants.CENTER);
            weekdayLabel.setBackground(settings.getColorBackgroundWeekdayLabels());
            weekdayLabel.setBorder(new EmptyBorder(0, 2, 0, 2));
            weekdayLabel.setOpaque(true);
            weekdayLabel.setText("wd" + i);
            CellConstraints constraints = CC.xywh(weekdayLabelColumnX, weekdayLabelRowY,
                    1, weekdayLabelHeightInCells);
            centerPanel.add(weekdayLabel, constraints);
            weekdayLabels.add(weekdayLabel);
        }
        // Add two blank extra weekday labels to cover up the beginning and the end of the row.
        weekdayLabelExtras = new ArrayList<JLabel>();
        {
            JLabel weekdayLabelExtra = new JLabel();
            weekdayLabelExtra.setHorizontalAlignment(SwingConstants.CENTER);
            weekdayLabelExtra.setVerticalAlignment(SwingConstants.CENTER);
            weekdayLabelExtra.setBackground(settings.getColorBackgroundWeekdayLabels());
            weekdayLabelExtra.setOpaque(true);
            CellConstraints constraints = CC.xywh((constantFirstWeekdayLabelCell.x - 1), weekdayLabelRowY,
                    1, weekdayLabelHeightInCells);
            centerPanel.add(weekdayLabelExtra, constraints);
            weekdayLabelExtras.add(weekdayLabelExtra);
        }
        {
            JLabel weekdayLabelExtra = new JLabel();
            weekdayLabelExtra.setHorizontalAlignment(SwingConstants.CENTER);
            weekdayLabelExtra.setVerticalAlignment(SwingConstants.CENTER);
            weekdayLabelExtra.setBackground(settings.getColorBackgroundWeekdayLabels());
            weekdayLabelExtra.setOpaque(true);
            CellConstraints constraints = CC.xywh((constantFirstWeekdayLabelCell.x + 7), weekdayLabelRowY,
                    1, weekdayLabelHeightInCells);
            centerPanel.add(weekdayLabelExtra, constraints);
            weekdayLabelExtras.add(weekdayLabelExtra);
        }
    }

    /**
     * addCalendarSelectionListener, This adds a calendar selection listener to this calendar panel.
     * For additional details, see the CalendarSelectionListener class documentation.
     */
    public void addCalendarSelectionListener(CalendarSelectionListener listener) {
        calendarSelectionListeners.add(listener);
    }

    /**
     * buttonNextMonthActionPerformed, This event is called when the next month button is pressed.
     * This sets the YearMonth of the calendar to the next month, and redraws the calendar.
     */
    private void buttonNextMonthActionPerformed(ActionEvent e) {
        drawCalendar(displayedYearMonth.plusMonths(1));
    }

    /**
     * buttonNextYearActionPerformed, This event is called when the next year button is pressed.
     * This sets the YearMonth of the calendar to the next year, and redraws the calendar.
     */
    private void buttonNextYearActionPerformed(ActionEvent e) {
        drawCalendar(displayedYearMonth.plusYears(1));
    }

    /**
     * buttonPreviousMonthActionPerformed, This event is called when the previous month button is
     * pressed. This sets the YearMonth of the calendar to the previous month, and redraws the
     * calendar.
     */
    private void buttonPreviousMonthActionPerformed(ActionEvent e) {
        drawCalendar(displayedYearMonth.minusMonths(1));
    }

    /**
     * buttonPreviousYearActionPerformed, This event is called when the previous year button is
     * pressed. This sets the YearMonth of the calendar to the previous year, and redraws the
     * calendar.
     */
    private void buttonPreviousYearActionPerformed(ActionEvent e) {
        drawCalendar(displayedYearMonth.minusYears(1));
    }

    /**
     * dateLabelMousePressed, This event is called any time that the user clicks on a date label in
     * the calendar. This sets the date picker to the selected date, and closes the calendar panel.
     */
    private void dateLabelMousePressed(MouseEvent e) {
        // Get the label that was clicked.
        JLabel label = (JLabel) e.getSource();
        // If the label is empty, do nothing and return.
        String labelText = label.getText();
        if ("".equals(labelText)) {
            return;
        }
        // We have a label with a specific date, so set the date and close the calendar.
        int dayOfMonth = Integer.parseInt(labelText);
        LocalDate clickedDate = LocalDate.of(
                displayedYearMonth.getYear(), displayedYearMonth.getMonth(), dayOfMonth);
        userSelectedADate(clickedDate);
    }

    /**
     * drawCalendar, This can be called to redraw the calendar. The calendar will be drawn with the
     * currently displayed year and month. This function should not normally need to be called by
     * the programmer, because the calendar will automatically redraw itself as needed.
     */
    public void drawCalendar() {
        drawCalendar(displayedYearMonth);
    }

    /**
     * drawCalendar, This is called whenever the calendar needs to be drawn. This takes a year and a
     * month to indicate which month should be drawn in the calendar.
     */
    private void drawCalendar(int year, Month month) {
        drawCalendar(YearMonth.of(year, month));
    }

    /**
     * drawCalendar, This is called whenever the calendar needs to be drawn. This takes a year and a
     * month to indicate which month should be drawn in the calendar.
     */
    private void drawCalendar(YearMonth yearMonth) {
        // Save the displayed yearMonth.
        this.displayedYearMonth = yearMonth;
        // Get the displayed month and year.
        Month displayedMonth = yearMonth.getMonth();
        int displayedYear = yearMonth.getYear();
        // Get an instance of the calendar symbols for the current locale.
        DateFormatSymbols symbols = DateFormatSymbols.getInstance(settings.getLocale());
        // Get the days of the week in the local language.
        String localShortDaysOfWeek[] = symbols.getShortWeekdays();
        // Get the full month names in the current locale.
        int zeroBasedMonthIndex = (displayedMonth.getValue() - 1);
        String localizedFullMonth = settings.getTranslationArrayStandaloneLongMonthNames()[zeroBasedMonthIndex];
        String localizedShortMonth = settings.getTranslationArrayStandaloneShortMonthNames()[zeroBasedMonthIndex];
        // Get the first day of the month, and the first day of week.
        LocalDate firstDayOfMonth = LocalDate.of(displayedYear, displayedMonth, 1);
        DayOfWeek firstDayOfWeekOfMonth = firstDayOfMonth.getDayOfWeek();
        // Get the last day of the month.
        int lastDateOfMonth = getLastDayOfMonth(displayedYearMonth);
        // Find out if we have a selected date that is inside the currently displayed month.
        boolean selectedDateIsInDisplayedMonth = (displayedSelectedDate != null)
                && (displayedSelectedDate.getYear() == displayedYear)
                && (displayedSelectedDate.getMonth() == displayedMonth);
        // Set the component colors
        Color calendarPanelBackgroundColor = settings.getColorBackgroundCalendarPanel();
        setBackground(calendarPanelBackgroundColor);
        headerControlsPanel.setBackground(calendarPanelBackgroundColor);
        monthAndYearOuterPanel.setBackground(calendarPanelBackgroundColor);
        footerPanel.setBackground(calendarPanelBackgroundColor);
        Color navigationButtonsColor = settings.getColorBackgroundNavigateYearMonthButtons();
        if (navigationButtonsColor != null) {
            buttonPreviousYear.setBackground(navigationButtonsColor);
            buttonNextYear.setBackground(navigationButtonsColor);
            buttonPreviousMonth.setBackground(navigationButtonsColor);
            buttonNextMonth.setBackground(navigationButtonsColor);
        }
        // Set the month and the year labels. 
        // Use the short month if the user is currently using the keyboard editor for the year.
        if (monthAndYearInnerPanel.isAncestorOf(yearEditorPanel)) {
            labelMonth.setText(localizedShortMonth);
        } else {
            labelMonth.setText(localizedFullMonth);
        }
        String displayedYearString = "" + displayedYear;
        labelYear.setText(displayedYearString);
        if (!displayedYearString.equals(yearTextField.getText())) {
            yearTextField.skipNotificationOfNumberChangeListenerWhileTrue = true;
            yearTextField.setText(displayedYearString);
            yearTextField.skipNotificationOfNumberChangeListenerWhileTrue = false;
        }
        // Set the days of the week labels, and create an array to represent the weekday positions.
        ArrayList<DayOfWeek> daysOfWeekAsDisplayed = new ArrayList<DayOfWeek>();
        int isoFirstDayOfWeekValue = settings.getFirstDayOfWeek().getValue();
        int isoLastDayOfWeekOverflowed = isoFirstDayOfWeekValue + 6;
        int weekdayLabelArrayIndex = 0;
        for (int dayOfWeek = isoFirstDayOfWeekValue; dayOfWeek <= isoLastDayOfWeekOverflowed; dayOfWeek++) {
            int localShortDaysOfWeekArrayIndex = (dayOfWeek % 7) + 1;
            int isoDayOfWeek = (dayOfWeek > 7) ? (dayOfWeek - 7) : dayOfWeek;
            DayOfWeek currentDayOfWeek = DayOfWeek.of(isoDayOfWeek);
            daysOfWeekAsDisplayed.add(currentDayOfWeek);
            weekdayLabels.get(weekdayLabelArrayIndex)
                    .setText(localShortDaysOfWeek[localShortDaysOfWeekArrayIndex]);
            ++weekdayLabelArrayIndex;
        }
        // Set the dates of the month labels.
        // Also save the label for the selected date, if one is present in the current month.
        boolean insideValidRange = false;
        int dayOfMonth = 1;
        JLabel selectedDateLabel = null;
        for (int dateLabelArrayIndex = 0; dateLabelArrayIndex < dateLabels.size(); ++dateLabelArrayIndex) {
            // Get the current date label.
            JLabel dateLabel = dateLabels.get(dateLabelArrayIndex);
            // Reset the state of every label to a default state.
            dateLabel.setBackground(Color.white);
            dateLabel.setForeground(Color.black);
            dateLabel.setBorder(null);
            dateLabel.setEnabled(true);
            dateLabel.setToolTipText(null);
            // Calculate the index to use on the daysOfWeekAsDisplayed array.
            int daysOfWeekAsDisplayedArrayIndex = dateLabelArrayIndex % 7;
            // Check to see if we are inside the valid range for days of this month.
            if (daysOfWeekAsDisplayed.get(daysOfWeekAsDisplayedArrayIndex) == firstDayOfWeekOfMonth
                    && dateLabelArrayIndex < 7) {
                insideValidRange = true;
            }
            if (dayOfMonth > lastDateOfMonth) {
                insideValidRange = false;
            }
            // While we are inside the valid range, set the date labels with the day of the month.
            if (insideValidRange) {
                // Get a local date object for the current date.
                LocalDate currentDate = LocalDate.of(displayedYear, displayedMonth, dayOfMonth);
                DateVetoPolicy vetoPolicy = settings.getVetoPolicy();
                DateHighlightPolicy highlightPolicy = settings.getHighlightPolicy();
                boolean dateIsVetoed = InternalUtilities.isDateVetoed(vetoPolicy, currentDate);
                HighlightInformation highlightInfo = null;
                if (highlightPolicy != null) {
                    highlightInfo = highlightPolicy.getHighlightInformationOrNull(currentDate);
                }
                if (dateIsVetoed) {
                    dateLabel.setEnabled(false);
                    dateLabel.setBackground(settings.getColorBackgroundVetoedDates());
                }
                if ((!dateIsVetoed) && (highlightInfo != null)) {
                    // Set the highlight background color (always).
                    Color colorBackground = settings.getColorBackgroundHighlightedDates();
                    if (highlightInfo.colorBackground != null) {
                        colorBackground = highlightInfo.colorBackground;
                    }
                    dateLabel.setBackground(colorBackground);
                    // If needed, set the highlight text color.
                    if (highlightInfo.colorText != null) {
                        dateLabel.setForeground(highlightInfo.colorText);
                    }
                    // If needed, set the highlight tooltip text.
                    if (highlightInfo.tooltipText != null
                            && (!(highlightInfo.tooltipText.isEmpty()))) {
                        dateLabel.setToolTipText(highlightInfo.tooltipText);
                    }
                }
                // If needed, save the label for the selected date.
                if (selectedDateIsInDisplayedMonth && displayedSelectedDate != null
                        && displayedSelectedDate.getDayOfMonth() == dayOfMonth) {
                    selectedDateLabel = dateLabel;
                }
                // Set the text for the current date.
                dateLabel.setText("" + dayOfMonth);
                ++dayOfMonth;
            } else {
                // We are not inside the valid range, so set this label to an empty string.
                dateLabel.setText("");
            }
        }
        // If needed, change the color of the selected date.
        if (selectedDateLabel != null) {
            selectedDateLabel.setBackground(new Color(163, 184, 204));
            selectedDateLabel.setBorder(new LineBorder(new Color(99, 130, 191)));
        }
        // Set the label for the today button.
        String todayDateString = settings.getFormatForTodayButton().format(LocalDate.now());
        String todayLabel = settings.getTranslationToday() + ":  " + todayDateString;
        labelSetDateToToday.setText(todayLabel);
        // If today is vetoed, disable the today button.
        DateVetoPolicy vetoPolicy = settings.getVetoPolicy();
        boolean todayIsVetoed = InternalUtilities.isDateVetoed(
                vetoPolicy, LocalDate.now());
        labelSetDateToToday.setEnabled(!todayIsVetoed);

        // If null is not allowed, then disable and hide the Clear label. 
        // Note: I had considered centering the today label in the CalendarPanel whenever the 
        // clear label was hidden. However, it still looks better when it is aligned to the left.
        boolean shouldEnableClearButton = settings.getAllowEmptyDates();
        labelClearDate.setEnabled(shouldEnableClearButton);
        labelClearDate.setVisible(shouldEnableClearButton);

        // Set the label for the clear button.
        labelClearDate.setText(settings.getTranslationClear());

        // Set the size of the month and year panel to be big enough to hold the largest month text.
        setSizeOfMonthYearPanel();

        // Set the size of the cell that contains the date panel.
        setSizeOfDatePanelCell();
    }

    /**
     * getCalendarSelectionListeners, This returns a new ArrayList, that contains any calendar
     * selection listeners that are registered with this CalendarPanel.
     */
    public ArrayList<CalendarSelectionListener> getCalendarSelectionListeners() {
        return new ArrayList<CalendarSelectionListener>(calendarSelectionListeners);
    }

    /**
     * getSelectedDate, This returns the date that is currently marked as "selected" in the
     * calendar. If no date is selected, then this will return null.
     *
     * This should not be confused with the "last valid date" of a DatePicker object. This function
     * would typically only be needed when the CalendarPanel class is being used independently from
     * the DatePicker class.
     */
    public LocalDate getSelectedDate() {
        return displayedSelectedDate;
    }

    /**
     * getLastDayOfMonth, This returns the last day of the month for the specified year and month.
     *
     * Implementation notes: As of this writing, the below implementation is verified to work
     * correctly for negative years, as those years are to defined in the iso 8601 your format that
     * is used by java.time.YearMonth. This functionality can be tested by by checking to see if to
     * see if the year "-0004" is correctly displayed as a leap year. Leap years have 29 days in
     * February. There should be 29 days in the month of "February 1, -0004".
     */
    private int getLastDayOfMonth(YearMonth yearMonth) {
        LocalDate firstDayOfMonth = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
        int lastDayOfMonth = firstDayOfMonth.lengthOfMonth();
        return lastDayOfMonth;
    }

    /**
     * getMonthOrYearMenuLocation, This calculates the position should be used to set the location
     * of the month or the year popup menus, relative to their source labels. These menus are used
     * to change the current month or current year from within the calendar panel.
     */
    private Point getMonthOrYearMenuLocation(JLabel sourceLabel, JPopupMenu filledPopupMenu) {
        Rectangle labelBounds = sourceLabel.getBounds();
        int menuHeight = filledPopupMenu.getPreferredSize().height;
        int popupX = labelBounds.x + labelBounds.width + 1;
        int popupY = labelBounds.y + (labelBounds.height / 2) - (menuHeight / 2);
        return new Point(popupX, popupY);
    }

    /**
     * labelClearDateMousePressed, This event is called when the "Clear" label is clicked in a date
     * picker. This sets the date picker date to an empty date. (This sets the last valid date to
     * null.)
     */
    private void labelClearDateMousePressed(MouseEvent e) {
        userSelectedADate(null);
    }

    /**
     * labelIndicatorMouseEntered, This event is called when the user move the mouse inside a
     * monitored label. This is used to generate mouse over effects for the calendar panel.
     */
    private void labelIndicatorMouseEntered(MouseEvent e) {
        JLabel label = ((JLabel) e.getSource());
        if (label == labelSetDateToToday) {
            DateVetoPolicy vetoPolicy = settings.getVetoPolicy();
            boolean todayIsVetoed = InternalUtilities.isDateVetoed(vetoPolicy, LocalDate.now());
            if (todayIsVetoed) {
                return;
            }
        }
        label.setBackground(new Color(184, 207, 229));
        label.setBorder(new CompoundBorder(
                new LineBorder(Color.GRAY), labelIndicatorEmptyBorder));
    }

    /**
     * labelIndicatorMouseExited, This event is called when the user move the mouse outside of a
     * monitored label. This is used to generate mouse over effects for the calendar panel.
     */
    private void labelIndicatorMouseExited(MouseEvent e) {
        JLabel label = ((JLabel) e.getSource());
        labelIndicatorSetColorsToDefaultState(label);
    }

    /**
     * labelIndicatorSetColorsToDefaultState, This event is called to set a label indicator to the
     * state it should have when there is no mouse hovering over it.
     */
    private void labelIndicatorSetColorsToDefaultState(JLabel label) {
        if (label == null || settings == null) {
            return;
        }
        if (label == labelMonth || label == labelYear) {
            label.setBackground(settings.getColorBackgroundMonthAndYear());
            monthAndYearInnerPanel.setBackground(settings.getColorBackgroundMonthAndYear());
        } else {
            label.setBackground(settings.getColorBackgroundTodayAndClear());
        }
        label.setBorder(new CompoundBorder(
                new EmptyBorder(1, 1, 1, 1), labelIndicatorEmptyBorder));
    }

    /**
     * labelMonthIndicatorMousePressed, This event is called any time that the user clicks on the
     * month display label in the calendar. This opens a menu that the user can use to select a new
     * month in the same year.
     */
    private void labelMonthIndicatorMousePressed(MouseEvent e) {
        JPopupMenu monthPopupMenu = new JPopupMenu();
        String[] allLocalMonths = settings.getTranslationArrayStandaloneLongMonthNames();
        for (int i = 0; i < allLocalMonths.length; ++i) {
            final String localMonth = allLocalMonths[i];
            final int localMonthZeroBasedIndexTemp = i;
            if (!localMonth.isEmpty()) {
                monthPopupMenu.add(new JMenuItem(new AbstractAction(localMonth) {
                    int localMonthZeroBasedIndex = localMonthZeroBasedIndexTemp;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        drawCalendar(displayedYearMonth.getYear(),
                                Month.of(localMonthZeroBasedIndex + 1));
                    }
                }));
            }
        }
        Point menuLocation = getMonthOrYearMenuLocation(labelMonth, monthPopupMenu);
        monthPopupMenu.show(monthAndYearInnerPanel, menuLocation.x, menuLocation.y);
    }

    /**
     * labelSetDateToTodayMousePressed, This event is called when the "Today" label is clicked in a
     * date picker. This sets the date picker date to today.
     */
    private void labelSetDateToTodayMousePressed(MouseEvent e) {
        userSelectedADate(LocalDate.now());
    }

    /**
     * labelYearIndicatorMousePressed, This event is called any time that the user clicks on the
     * year display label in the calendar. This opens a menu that the user can use to select a new
     * year within a chosen range of the previously displayed year.
     */
    private void labelYearIndicatorMousePressed(MouseEvent e) {
        int firstYearDifference = -11;
        int lastYearDifference = +11;
        JPopupMenu yearPopupMenu = new JPopupMenu();
        for (int yearDifference = firstYearDifference; yearDifference <= lastYearDifference;
                ++yearDifference) {
            // No special processing is required for the BC to AD transition in the 
            // ISO 8601 calendar system. Year zero does exist in this system.
            YearMonth choiceYearMonth = displayedYearMonth.plusYears(yearDifference);
            String choiceYearMonthString = "" + choiceYearMonth.getYear();
            yearPopupMenu.add(new JMenuItem(new AbstractAction(choiceYearMonthString) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String chosenMenuText = ((JMenuItem) e.getSource()).getText();
                    int chosenYear = Integer.parseInt(chosenMenuText);
                    drawCalendar(chosenYear, displayedYearMonth.getMonth());
                }
            }));
        }
        String choiceOtherYearString = "( . . . )";
        yearPopupMenu.add(new JMenuItem(new AbstractAction(choiceOtherYearString) {
            @Override
            public void actionPerformed(ActionEvent e) {
                otherYearMenuItemClicked();
            }
        }));
        Point menuLocation = getMonthOrYearMenuLocation(labelYear, yearPopupMenu);
        yearPopupMenu.show(monthAndYearInnerPanel, menuLocation.x, menuLocation.y);
    }

    private void doneEditingYearButtonActionPerformed(ActionEvent e) {
        monthAndYearInnerPanel.remove(yearEditorPanel);
        labelYear.setEnabled(true);
        labelYear.setVisible(true);
        drawCalendar(displayedYearMonth);
    }

    private void otherYearMenuItemClicked() {
        labelYear.setEnabled(false);
        labelYear.setVisible(false);
        monthAndYearInnerPanel.add(yearEditorPanel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        drawCalendar(displayedYearMonth);
        yearTextField.requestFocusInWindow();
    }

    /**
     * removeCalendarSelectionListener, This removes the specified calendar selection listener from
     * this CalendarPanel.
     */
    public void removeCalendarSelectionListener(CalendarSelectionListener listener) {
        calendarSelectionListeners.remove(listener);
    }

    /**
     * setSelectedDate, This sets a date that will be marked as "selected" in the calendar. The
     * selectedDate will only be visible when the matching YearMonth is being displayed in the
     * calendar. Note that this function does -not- change the currently displayed YearMonth.
     */
    public void setSelectedDate(LocalDate selectedDate) {
        // Save the selected date, redraw the calendar, and notify any listeners.
        zInternalChangeSelectedDateProcedure(selectedDate);
    }

    /**
     * setDisplayedYearMonth, This sets the year and month that is currently displayed in the
     * calendar. The yearMonth can not be set to null. If the parameter is null, an exception will
     * be thrown. Note that this function does -not- change the displayed selected date.
     */
    public void setDisplayedYearMonth(YearMonth yearMonth) {
        if (yearMonth == null) {
            throw new RuntimeException("CalendarPanel.setDisplayedYearMonth(), "
                    + "The displayed year and month cannot be set to null.");
        }
        drawCalendar(yearMonth);
    }

    /**
     * setSizeOfDatePanelCell, This sets the size of the cell holds date panel. By default, the size
     * is set in such a way that the grid layout of the date panel will always touch the inside
     * edges of the cell. At the time of this writing, the cell that contains the date panel is:
     * x=1, y=3, in the calendar panel.
     */
    private void setSizeOfDatePanelCell() {
        // Get the minimum desired size of the date panel.
        int minimumHeight = settings.getSizeDatePanelMinimumHeight();
        int minimumWidth = settings.getSizeDatePanelMinimumWidth();
        // Get the layout for the calendar panel.
        GridBagLayout layout = ((GridBagLayout) getLayout());
        // Initialize the minimum height and width of the date panel cell.
        int panelHeight = minimumHeight;
        int panelWidth = minimumWidth;
        // Force the new size to be multiples of 7 for the columns and rows.
        panelHeight += (panelHeight % 7);
        panelWidth += (panelWidth % 7);
        // Set the containing cell to be the desired minimum size.
        layout.rowHeights[3] = panelHeight + constantSizeOfCenterPanelBorders.height;
        layout.columnWidths[1] = panelWidth + constantSizeOfCenterPanelBorders.width;
        // Ask the components to layout and redraw themselves.
        this.doLayout();
        this.validate();
    }

    /**
     * setSizeOfMonthYearPanel, This sets the size of the panel at the top of the calendar that
     * holds the month and the year label. The size is calculated from the largest month name (in
     * pixels), that exists in locale and language that is being used by the date picker.
     */
    private void setSizeOfMonthYearPanel() {
        // Get the font metrics object.
        Font font = labelMonth.getFont();
        Canvas canvas = new Canvas();
        FontMetrics metrics = canvas.getFontMetrics(font);
        // Get the height of a line of text in this font.
        int height = metrics.getHeight();
        // Get the length of the longest translated month string (in pixels).
        DateFormatSymbols symbols = DateFormatSymbols.getInstance(settings.getLocale());
        String[] allLocalMonths = symbols.getMonths();
        int longestMonthPixels = 0;
        for (String month : allLocalMonths) {
            int monthPixels = metrics.stringWidth(month);
            longestMonthPixels = (monthPixels > longestMonthPixels) ? monthPixels : longestMonthPixels;
        }
        int yearPixels = metrics.stringWidth("_2000");
        // Calculate the size of a box to hold the text with some padding.
        Dimension size = new Dimension(longestMonthPixels + yearPixels + 12, height + 2);
        // Set the monthAndYearPanel to the appropriate constant size.
        monthAndYearOuterPanel.setMinimumSize(size);
        monthAndYearOuterPanel.setMaximumSize(size);
        monthAndYearOuterPanel.setPreferredSize(size);
        // Redraw the panel.
        this.doLayout();
        this.validate();
    }

    /**
     * userSelectedADate, This is called any time that the user makes a date selection on the
     * calendar panel, including choosing to clear the date. If this calendar panel is being used
     * inside of a DatePicker, then this will save the selected date and close the calendar. The
     * only time this function will not be called during an exit event, is if the user left focus of
     * the component or pressed escape to cancel choosing a new date.
     */
    private void userSelectedADate(LocalDate selectedDate) {
        // If a date was selected and the date is vetoed, do nothing.
        if (selectedDate != null) {
            DateVetoPolicy vetoPolicy = settings.getVetoPolicy();
            if (InternalUtilities.isDateVetoed(vetoPolicy, selectedDate)) {
                return;
            }
        }
        // Save the selected year and month, in case it is needed later.
        if (selectedDate != null) {
            YearMonth selectedDateYearMonth = YearMonth.from(selectedDate);
            displayedYearMonth = selectedDateYearMonth;
        } else {
            // The selected date was cleared, so set the displayed month and year to today's values.
            displayedYearMonth = YearMonth.now();
        }

        // Save the selected date, redraw the calendar, and notify any listeners.
        zInternalChangeSelectedDateProcedure(selectedDate);

        // If this calendar panel is associated with a date picker, then set the DatePicker date
        // and close the popup.
        if (settings.getParentDatePicker() != null) {
            DatePicker parent = settings.getParentDatePicker();
            parent.setDate(selectedDate);
            parent.closePopup();
        }
    }

    /**
     * zAddMouseListenersToTodayAndClearButtons, This adds the needed mouse listeners to the today
     * button and the clear button.
     */
    private void zAddMouseListenersToTodayAndClearButtons() {
        labelSetDateToToday.addMouseListener(new MouseLiberalAdapter() {
            @Override
            public void mouseLiberalClick(MouseEvent e) {
                labelSetDateToTodayMousePressed(e);
            }

            @Override
            public void mouseEnter(MouseEvent e) {
                labelIndicatorMouseEntered(e);
            }

            @Override
            public void mouseExit(MouseEvent e) {
                labelIndicatorMouseExited(e);
            }
        });
        labelClearDate.addMouseListener(new MouseLiberalAdapter() {
            @Override
            public void mouseLiberalClick(MouseEvent e) {
                labelClearDateMousePressed(e);
            }

            @Override
            public void mouseEnter(MouseEvent e) {
                labelIndicatorMouseEntered(e);
            }

            @Override
            public void mouseExit(MouseEvent e) {
                labelIndicatorMouseExited(e);
            }
        });
    }

    /**
     * zInternalChangeSelectedDateProcedure, This should be called whenever we need to change the
     * selected date variable. This will store the supplied selected date and redraw the calendar.
     * If needed, this will notify all calendar selection listeners that the selected date has been
     * changed. This does not perform any other tasks besides those described here.
     *
     * By intention, this will fire an event even if the user selects the same value twice. This is
     * so that programmers can catch all user interactions of interest to them. Duplicate events can
     * be detected by using the function CalendarSelectionEvent.isDuplicate().
     */
    private void zInternalChangeSelectedDateProcedure(LocalDate newDate) {
        LocalDate oldDate = displayedSelectedDate;
        displayedSelectedDate = newDate;
        drawCalendar(displayedYearMonth);
        for (CalendarSelectionListener calendarSelectionListener : calendarSelectionListeners) {
            CalendarSelectionEvent dateSelectionEvent = new CalendarSelectionEvent(
                    this, newDate, oldDate);
            calendarSelectionListener.selectionChanged(dateSelectionEvent);
        }
    }

    /**
     * zLabelIndicatorsAllSetColorsToDefaultState, This is called to set all label indicators to the
     * state they should have when there is no mouse hovering over them.
     */
    public void zLabelIndicatorsAllSetColorsToDefaultState() {
        labelIndicatorSetColorsToDefaultState(labelMonth);
        labelIndicatorSetColorsToDefaultState(labelYear);
        labelIndicatorSetColorsToDefaultState(labelSetDateToToday);
        labelIndicatorSetColorsToDefaultState(labelClearDate);
    }

    /**
     * zRedrawWeekdayLabelColors, This is called to redraw the weekday label colors any time that
     * those colors are changed in the settings.
     */
    public void zRedrawWeekdayLabelColors() {
        for (JLabel weekdayLabel : weekdayLabels) {
            weekdayLabel.setBackground(settings.getColorBackgroundWeekdayLabels());
            weekdayLabel.repaint();
        }
        for (JLabel weekdayLabelExtra : weekdayLabelExtras) {
            weekdayLabelExtra.setBackground(settings.getColorBackgroundWeekdayLabels());
            weekdayLabelExtra.repaint();
        }
    }

    /**
     * integerTextFieldNumberChanged, This function is required for the implementation of
     * IntegerTextFieldNumberChangeListener. This is called whenever the number in the integer text
     * field has changed.
     */
    @Override
    public void integerTextFieldNumberChanged(JIntegerTextField source, int newValue) {
        YearMonth newYearMonth = YearMonth.of(newValue, displayedYearMonth.getMonth());
        drawCalendar(newYearMonth);
    }

    /**
     * initComponents, This initializes the GUI components in the calendar panel. This function is
     * automatically generated by JFormDesigner. This function should not be modified by hand, it
     * should only be modified from within JFormDesigner.
     */
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		headerControlsPanel = new JPanel();
		buttonPreviousYear = new JButton();
		buttonPreviousMonth = new JButton();
		monthAndYearOuterPanel = new JPanel();
		monthAndYearInnerPanel = new JPanel();
		labelMonth = new JLabel();
		labelYear = new JLabel();
		buttonNextMonth = new JButton();
		buttonNextYear = new JButton();
		centerPanel = new JPanel();
		footerPanel = new JPanel();
		labelSetDateToToday = new JLabel();
		labelClearDate = new JLabel();
		yearEditorPanel = new JPanel();
		doneEditingYearButton = new JButton();

		//======== this ========
		setLayout(new GridBagLayout());
		((GridBagLayout)getLayout()).columnWidths = new int[] {5, 0, 5, 0};
		((GridBagLayout)getLayout()).rowHeights = new int[] {6, 0, 5, 80, 5, 0, 5, 0};
		((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
		((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

		//======== headerControlsPanel ========
		{
			headerControlsPanel.setLayout(new FormLayout(
				"3*(pref), pref:grow, 3*(pref)",
				"fill:pref"));
			((FormLayout)headerControlsPanel.getLayout()).setColumnGroups(new int[][] {{1, 2, 6, 7}});

			//---- buttonPreviousYear ----
			buttonPreviousYear.setText("<<");
			buttonPreviousYear.setFocusable(false);
			buttonPreviousYear.setFocusPainted(false);
			buttonPreviousYear.setHorizontalTextPosition(SwingConstants.CENTER);
			buttonPreviousYear.setMargin(new Insets(5, 6, 5, 6));
			buttonPreviousYear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonPreviousYearActionPerformed(e);
				}
			});
			headerControlsPanel.add(buttonPreviousYear, CC.xy(1, 1));

			//---- buttonPreviousMonth ----
			buttonPreviousMonth.setText("<");
			buttonPreviousMonth.setFocusable(false);
			buttonPreviousMonth.setFocusPainted(false);
			buttonPreviousMonth.setHorizontalTextPosition(SwingConstants.CENTER);
			buttonPreviousMonth.setMargin(new Insets(5, 6, 5, 6));
			buttonPreviousMonth.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonPreviousMonthActionPerformed(e);
				}
			});
			headerControlsPanel.add(buttonPreviousMonth, CC.xy(2, 1));

			//======== monthAndYearOuterPanel ========
			{
				monthAndYearOuterPanel.setLayout(new GridBagLayout());
				((GridBagLayout)monthAndYearOuterPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
				((GridBagLayout)monthAndYearOuterPanel.getLayout()).rowHeights = new int[] {0, 0};
				((GridBagLayout)monthAndYearOuterPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
				((GridBagLayout)monthAndYearOuterPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

				//======== monthAndYearInnerPanel ========
				{
					monthAndYearInnerPanel.setLayout(new GridBagLayout());
					((GridBagLayout)monthAndYearInnerPanel.getLayout()).columnWidths = new int[] {0, 1, 0, 0};
					((GridBagLayout)monthAndYearInnerPanel.getLayout()).rowHeights = new int[] {0, 0};
					((GridBagLayout)monthAndYearInnerPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
					((GridBagLayout)monthAndYearInnerPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

					//---- labelMonth ----
					labelMonth.setText("September");
					labelMonth.setHorizontalAlignment(SwingConstants.RIGHT);
					labelMonth.setOpaque(true);
					labelMonth.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseEntered(MouseEvent e) {
							labelIndicatorMouseEntered(e);
						}
						@Override
						public void mouseExited(MouseEvent e) {
							labelIndicatorMouseExited(e);
						}
						@Override
						public void mousePressed(MouseEvent e) {
							labelMonthIndicatorMousePressed(e);
						}
					});
					monthAndYearInnerPanel.add(labelMonth, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- labelYear ----
					labelYear.setText("2100");
					labelYear.setOpaque(true);
					labelYear.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseEntered(MouseEvent e) {
							labelIndicatorMouseEntered(e);
						}
						@Override
						public void mouseExited(MouseEvent e) {
							labelIndicatorMouseExited(e);
						}
						@Override
						public void mousePressed(MouseEvent e) {
							labelYearIndicatorMousePressed(e);
						}
					});
					monthAndYearInnerPanel.add(labelYear, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				}
				monthAndYearOuterPanel.add(monthAndYearInnerPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			headerControlsPanel.add(monthAndYearOuterPanel, CC.xy(4, 1));

			//---- buttonNextMonth ----
			buttonNextMonth.setText(">");
			buttonNextMonth.setFocusable(false);
			buttonNextMonth.setFocusPainted(false);
			buttonNextMonth.setHorizontalTextPosition(SwingConstants.CENTER);
			buttonNextMonth.setMargin(new Insets(5, 6, 5, 6));
			buttonNextMonth.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonNextMonthActionPerformed(e);
				}
			});
			headerControlsPanel.add(buttonNextMonth, CC.xy(6, 1));

			//---- buttonNextYear ----
			buttonNextYear.setText(">>");
			buttonNextYear.setFocusable(false);
			buttonNextYear.setFocusPainted(false);
			buttonNextYear.setHorizontalTextPosition(SwingConstants.CENTER);
			buttonNextYear.setMargin(new Insets(5, 6, 5, 6));
			buttonNextYear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonNextYearActionPerformed(e);
				}
			});
			headerControlsPanel.add(buttonNextYear, CC.xy(7, 1));
		}
		add(headerControlsPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

		//======== centerPanel ========
		{
			centerPanel.setBackground(new Color(99, 130, 191));
			centerPanel.setLayout(new FormLayout(
				"1px, 7*(default:grow), 1px",
				"2px, fill:default:grow, 2*(1px), 6*(fill:default:grow), 1px"));
			((FormLayout)centerPanel.getLayout()).setColumnGroups(new int[][] {{2, 3, 4, 5, 6, 7, 8}});
			((FormLayout)centerPanel.getLayout()).setRowGroups(new int[][] {{2, 5, 6, 7, 8, 9, 10}});
		}
		add(centerPanel, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

		//======== footerPanel ========
		{
			footerPanel.setLayout(new GridBagLayout());
			((GridBagLayout)footerPanel.getLayout()).columnWidths = new int[] {6, 0, 0, 0, 6, 0};
			((GridBagLayout)footerPanel.getLayout()).rowHeights = new int[] {0, 0};
			((GridBagLayout)footerPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
			((GridBagLayout)footerPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

			//---- labelSetDateToToday ----
			labelSetDateToToday.setText("Today: Feb 12, 2016");
			labelSetDateToToday.setHorizontalAlignment(SwingConstants.CENTER);
			labelSetDateToToday.setOpaque(true);
			footerPanel.add(labelSetDateToToday, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

			//---- labelClearDate ----
			labelClearDate.setText("Clear");
			labelClearDate.setOpaque(true);
			footerPanel.add(labelClearDate, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		}
		add(footerPanel, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

		//======== yearEditorPanel ========
		{
			yearEditorPanel.setLayout(new GridBagLayout());
			((GridBagLayout)yearEditorPanel.getLayout()).columnWidths = new int[] {40, 1, 26, 0};
			((GridBagLayout)yearEditorPanel.getLayout()).rowHeights = new int[] {0, 0};
			((GridBagLayout)yearEditorPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
			((GridBagLayout)yearEditorPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

			//---- doneEditingYearButton ----
			doneEditingYearButton.setFocusPainted(false);
			doneEditingYearButton.setFocusable(false);
			doneEditingYearButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					doneEditingYearButtonActionPerformed(e);
				}
			});
			yearEditorPanel.add(doneEditingYearButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		}
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

}
