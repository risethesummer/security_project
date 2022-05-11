package common.gui.table.cells;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.Properties;

/**
 * gui.table.cells
 * Created by NhatLinh - 19127652
 * Date 4/24/2022 - 3:52 PM
 * Description: ...
 */
public class DatePickerCell implements ICell {

    public UtilDateModel getDateModel() {
        return dateModel;
    }

    private final UtilDateModel dateModel = new UtilDateModel();
    private final JDatePickerImpl picker;
    private final ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            Date now = Date.from(Instant.now());
            Calendar c = Calendar.getInstance();
            c.setTime(now);
            if (!dateModel.getValue().after(now))
                dateModel.setValue(c.getTime());
        }
    };

    public DatePickerCell() {
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        Date now = Date.from(Instant.now());
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        dateModel.setValue(c.getTime());
        dateModel.addChangeListener(changeListener);

        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, p);
        picker = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {
            private final String datePattern = "yyyy-MM-dd";
            private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
            @Override
            public Object stringToValue(String text) throws ParseException {
                return dateFormatter.parseObject(text);
            }
            @Override
            public String valueToString(Object value) throws ParseException {
                if (value != null) {
                    Calendar cal = (Calendar) value;
                    return dateFormatter.format(cal.getTime());
                }
                return "";
            }
        });
    }

    public DatePickerCell(boolean notAdd)
    {
        this();
        dateModel.removeChangeListener(changeListener);
    }

    @Override
    public Component getComponent() {
        return picker;
    }
}
