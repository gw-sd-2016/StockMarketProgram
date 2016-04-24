package objects;

import java.util.Date;

public class PressRelease {

	protected String title;
	protected String content;
	protected Date date;

	public PressRelease(String title, String content, Date date) {
		this.title = title;
		this.content = content;
		this.date = date;
	}

	public String getTitle() {

		return title;
	}

	public String getContent() {

		return content;
	}

	public Date getDate() {

		return date;
	}

	public void setDate(Date date) {

		this.date = date;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public void setContent(String content) {

		this.content = content;
	}
}
