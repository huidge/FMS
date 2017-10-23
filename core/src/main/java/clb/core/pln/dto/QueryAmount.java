package clb.core.pln.dto;

public class QueryAmount {

	private int totalAmount;
	
	private int usedAmount;
	
	private int avilAmount;
	
	private String channelName;
	
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getUsedAmount() {
		return usedAmount;
	}

	public void setUsedAmount(int usedAmount) {
		this.usedAmount = usedAmount;
	}

	public int getAvilAmount() {
		return avilAmount;
	}

	public void setAvilAmount(int avilAmount) {
		this.avilAmount = avilAmount;
	}

	 
	
}
