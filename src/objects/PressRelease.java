package objects;

import java.util.concurrent.atomic.AtomicInteger;

public class PressRelease {

	protected String title;
	protected int id;
	protected String content;
	static AtomicInteger nextId = new AtomicInteger();

	public PressRelease(String title, String content) {
		this.title = title;
		this.id = nextId.incrementAndGet();
		this.content = content;
	}

	public String getTitle() {

		return title;
	}

	public String getContent() {

		return content;
	}

	public int getID() {

		return id;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public void setContent(String content) {

		this.content = content;
	}
}
