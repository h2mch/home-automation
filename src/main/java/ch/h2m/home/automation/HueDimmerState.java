package ch.h2m.home.automation;

import java.util.Calendar;

public class HueDimmerState {

    private final int buttonEvent;
    private final Calendar calendar;

    public HueDimmerState(int buttonEvent, Calendar calendar) {
        this.buttonEvent = buttonEvent;
        this.calendar = calendar;
    }

    public int getButtonEvent() {
        return buttonEvent;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HueDimmerState that = (HueDimmerState) o;

        if (buttonEvent != that.buttonEvent) return false;
        return calendar != null ? calendar.equals(that.calendar) : that.calendar == null;
    }

    @Override
    public int hashCode() {
        int result = buttonEvent;
        result = 31 * result + (calendar != null ? calendar.hashCode() : 0);
        return result;
    }
}
