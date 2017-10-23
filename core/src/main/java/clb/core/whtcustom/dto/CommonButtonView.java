package clb.core.whtcustom.dto;

//type = view
public class CommonButtonView extends Button {  
    private String type;  
    private String url;  
    
    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {  
        return type;  
    }  
  
    public void setType(String type) {  
        this.type = type;  
    }  
}