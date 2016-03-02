package objects;

public class GeneralData {
	protected String range;
	protected String companyName;
	protected String exchange;
	protected String avgVolume;

	public GeneralData(String range, String companyName, String exchange, String avgVolume) {
		this.range = range;
		this.companyName = companyName;
		this.exchange = exchange;
		this.avgVolume = avgVolume;
	}

	public String getRange() {

		return range;
	}

	public String getExchange() {

		return exchange;
	}

	public String getCompanyName() {

		return companyName;
	}

	public String getAvgVolume() {

		return avgVolume;
	}

	public void setRange(String range) {

		this.range = range;
	}

	public void setCompanyName(String companyName) {

		this.companyName = companyName;
	}

	public void setExchange(String exchange) {

		this.exchange = exchange;
	}

	public void setAvgVolume(String avgVolume) {

		this.avgVolume = avgVolume;
	}
}
