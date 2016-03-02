package objects;

import java.util.Calendar;
import java.util.Locale;

public class VolumeDate {
	protected Calendar date;
	protected double volume;
	protected double open;
	protected double close;
	protected double high;
	protected double low;

	public VolumeDate(Calendar date, double volume, double open, double close, double high, double low) {
		this.date = date;
		this.volume = volume;
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
	}

	public Calendar getCalendar() {

		return date;
	}

	public int getYear() {

		return date.get(Calendar.YEAR);
	}

	public int getMonth() {

		return date.get(Calendar.MONTH) + 1;
	}

	public int getDay() {

		return date.get(Calendar.DAY_OF_MONTH);
	}

	public double getVolume() {

		return volume;
	}

	public String getDateAsString() {

		return String.format("%s %d, %d", date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
				getDay(), getYear());
	}

	public double getOpen() {

		return open;
	}

	public double getHigh() {

		return high;
	}

	public double getLow() {

		return low;
	}

	public double getClose() {

		return close;
	}

	public void setCalendar(Calendar cal) {

		this.date = cal;
	}

	public void setVolume(int volume) {

		this.volume = volume;
	}

	public void setOpen(double open) {

		this.open = open;
	}

	public void setHigh(double high) {

		this.high = high;
	}

	public void setLow(double low) {

		this.low = low;
	}

	public void setClose(double close) {

		this.close = close;
	}
}
